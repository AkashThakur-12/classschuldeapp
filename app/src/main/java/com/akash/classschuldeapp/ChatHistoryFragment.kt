package com.akash.classschuldeapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatHistoryFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = view.findViewById<RecyclerView>(R.id.historyRecycler)
        val emptyText = view.findViewById<TextView>(R.id.emptyHistoryText)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val localSessions = ChatLocalStorage.getSessions(requireContext()).sortedByDescending { it.timestamp }
        val sessions = localSessions.mapNotNull { session ->
            val firstMsg = session.firstMessage
            // to fetch bot reply we can just look up messages
            val messages = ChatLocalStorage.getMessages(requireContext(), session.sessionId)
            val botReply = messages.find { !it.isUser }?.text ?: ""
            Triple(firstMsg, botReply, session.timestamp)
        }
        if (sessions.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.adapter = ChatHistoryAdapter(sessions)
        }
    }
}
