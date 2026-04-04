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

        val uid = auth.currentUser?.uid ?: return

        db.collection("chat_history")
            .whereEqualTo("userId", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                if (!isAdded) return@addOnSuccessListener
                val sessions = result.documents.mapNotNull { doc ->
                    val userMsg = doc.getString("userMessage") ?: return@mapNotNull null
                    val botReply = doc.getString("botReply") ?: ""
                    val ts = doc.getLong("timestamp") ?: 0L
                    Triple(userMsg, botReply, ts)
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
}
