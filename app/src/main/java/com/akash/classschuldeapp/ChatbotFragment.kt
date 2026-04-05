package com.akash.classschuldeapp

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import java.util.UUID

class ChatbotFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var drawerRecycler: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    
    private lateinit var attachmentPreview: ImageView
    private lateinit var removePreviewButton: ImageView
    private lateinit var attachButton: ImageView
    
    private lateinit var chatViewModel: ChatViewModel
    private val messages get() = chatViewModel.messages
    private lateinit var adapter: ChatAdapter
    private lateinit var drawerAdapter: DrawerChatAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private var currentImageUri: Uri? = null

    companion object {
        fun newInstance() = ChatbotFragment()
    }

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            currentImageUri = uri
            attachmentPreview.setImageURI(uri)
            attachmentPreview.visibility = View.VISIBLE
            removePreviewButton.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel = ViewModelProvider(requireActivity()).get(ChatViewModel::class.java)

        drawerLayout = view.findViewById(R.id.drawerLayout)
        recyclerView = view.findViewById(R.id.chatRecycler)
        drawerRecycler = view.findViewById(R.id.drawerRecycler)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)
        
        attachmentPreview = view.findViewById(R.id.attachmentPreview)
        removePreviewButton = view.findViewById(R.id.removePreviewButton)
        attachButton = view.findViewById(R.id.attachButton)

        val menuButton = view.findViewById<ImageView>(R.id.menuButton)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            loadSessions()
        }

        val newChatButton = view.findViewById<TextView>(R.id.newChatButton)
        newChatButton.setOnClickListener {
            startNewSession()
        }
        
        attachButton.setOnClickListener {
            pickMedia.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        
        removePreviewButton.setOnClickListener {
            clearAttachment()
        }

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter
        
        drawerAdapter = DrawerChatAdapter(emptyList()) { session ->
            loadSessionMessages(session.sessionId)
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        drawerRecycler.layoutManager = LinearLayoutManager(requireContext())
        drawerRecycler.adapter = drawerAdapter

        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isEmpty() && currentImageUri == null) return@setOnClickListener
            messageInput.text.clear()
            sendMessage(text)
        }
        
        // Initial load of sessions if empty
        if (messages.isEmpty()) {
            loadSessions()
        }
    }
    
    private fun startNewSession() {
        chatViewModel.currentSessionId = UUID.randomUUID().toString()
        messages.clear()
        adapter.notifyDataSetChanged()
        clearAttachment()
    }
    
    private fun clearAttachment() {
        currentImageUri = null
        attachmentPreview.visibility = View.GONE
        removePreviewButton.visibility = View.GONE
    }

    private fun loadSessions() {
        if (!isAdded) return
        val localSessions = ChatLocalStorage.getSessions(requireContext())
        val sortedSessions = localSessions.sortedByDescending { it.timestamp }
        drawerAdapter.updateSessions(sortedSessions)
        
        // Auto-load latest session messages if we have no messages currently
        if (messages.isEmpty() && sortedSessions.isNotEmpty()) {
            loadSessionMessages(sortedSessions.first().sessionId)
        }
    }
    
    private fun loadSessionMessages(sessionId: String) {
        if (!isAdded) return
        chatViewModel.currentSessionId = sessionId
        messages.clear()
        
        val localMessages = ChatLocalStorage.getMessages(requireContext(), sessionId)
        messages.addAll(localMessages)
        
        adapter.notifyDataSetChanged()
        if (messages.isNotEmpty()) {
            recyclerView.scrollToPosition(messages.size - 1)
        }
    }

    private fun sendMessage(userText: String) {
        if (!isAdded) return
        
        val uriToSave = currentImageUri?.toString()
        var bitmap: Bitmap? = null
        if (currentImageUri != null) {
            try {
                bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, currentImageUri!!))
                } else {
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, currentImageUri)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
        
        val userMsgObj = ChatMessage(userText, isUser = true, sessionId = chatViewModel.currentSessionId, imageUri = uriToSave)
        messages.add(userMsgObj)
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)
        
        // Save user message to local storage
        if (isAdded) {
            ChatLocalStorage.saveMessage(requireContext(), chatViewModel.currentSessionId, userMsgObj)
            val sessionItem = ChatSessionItem(chatViewModel.currentSessionId, if (userText.isNotEmpty()) userText else "Image Attachment", System.currentTimeMillis())
            ChatLocalStorage.saveSession(requireContext(), sessionItem)
            
            // Refresh drawer to show new session
            val localSessions = ChatLocalStorage.getSessions(requireContext())
            drawerAdapter.updateSessions(localSessions.sortedByDescending { it.timestamp })
        }
        
        clearAttachment()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(
                    content {
                        if (bitmap != null) {
                            image(bitmap)
                        }
                        text(userText)
                    }
                )
                val botReply = response.text ?: "Sorry, I couldn't understand that."

                if (!isAdded) return@launch
                val botMsgObj = ChatMessage(botReply, isUser = false, sessionId = chatViewModel.currentSessionId)
                messages.add(botMsgObj)
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                // Save bot message to local storage
                ChatLocalStorage.saveMessage(requireContext(), chatViewModel.currentSessionId, botMsgObj)

                val uid = auth.currentUser?.uid ?: return@launch
                val sessionData = hashMapOf<String, Any>(
                    "userId" to uid,
                    "sessionId" to chatViewModel.currentSessionId,
                    "userMessage" to userText,
                    "botReply" to botReply,
                    "timestamp" to System.currentTimeMillis()
                )
                if (uriToSave != null) {
                    sessionData["imageUri"] = uriToSave
                }
                db.collection("chat_history").add(sessionData)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (isAdded) {
                    context?.let { ctx ->
                        Toast.makeText(ctx, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
