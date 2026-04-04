package com.akash.classschuldeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class ChatbotFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private val messages = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        fun newInstance() = ChatbotFragment()
    }

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chatRecycler)
        messageInput = view.findViewById(R.id.messageInput)
        sendButton = view.findViewById(R.id.sendButton)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            messageInput.text.clear()
            sendMessage(text)
        }
    }

    private fun sendMessage(userText: String) {
        if (!isAdded) return
        messages.add(ChatMessage(userText, isUser = true))
        adapter.notifyItemInserted(messages.size - 1)
        recyclerView.scrollToPosition(messages.size - 1)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(userText)
                val botReply = response.text ?: "Sorry, I couldn't understand that."

                if (!isAdded) return@launch
                messages.add(ChatMessage(botReply, isUser = false))
                adapter.notifyItemInserted(messages.size - 1)
                recyclerView.scrollToPosition(messages.size - 1)

                // Save to Firestore
                val uid = auth.currentUser?.uid ?: return@launch
                val sessionData = hashMapOf(
                    "userId" to uid,
                    "userMessage" to userText,
                    "botReply" to botReply,
                    "timestamp" to System.currentTimeMillis()
                )
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
