package com.akash.classschuldeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DrawerChatAdapter(
    private var sessions: List<ChatSessionItem>,
    private val onSessionClick: (ChatSessionItem) -> Unit
) : RecyclerView.Adapter<DrawerChatAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.drawerSessionTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_drawer_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = sessions[position]
        holder.title.text = if (session.firstMessage.length > 25) {
            session.firstMessage.take(25) + "..."
        } else {
            session.firstMessage
        }
        
        holder.itemView.setOnClickListener {
            onSessionClick(session)
        }
    }

    fun updateSessions(newSessions: List<ChatSessionItem>) {
        sessions = newSessions
        notifyDataSetChanged()
    }

    override fun getItemCount() = sessions.size
}
