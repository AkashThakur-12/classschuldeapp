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

    private lateinit var recycler: RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler = view.findViewById(R.id.historyRecycler)
        emptyText = view.findViewById(R.id.emptyHistoryText)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        refreshHistory()
    }

    private fun refreshHistory() {
        if (!isAdded) return
        val localSessions = ChatLocalStorage.getSessions(requireContext()).sortedByDescending { it.timestamp }
        val sessions = localSessions.map { session ->
            val messages = ChatLocalStorage.getMessages(requireContext(), session.sessionId)
            val botReply = messages.find { !it.isUser }?.text ?: ""
            Pair(session, botReply)
        }

        if (sessions.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.adapter = ChatHistoryAdapter(sessions) { sessionToDelete ->
                ChatLocalStorage.deleteSession(requireContext(), sessionToDelete.sessionId)
                refreshHistory()
            }
        }
    }
}
