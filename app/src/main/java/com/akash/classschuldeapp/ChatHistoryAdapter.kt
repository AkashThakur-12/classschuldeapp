package com.akash.classschuldeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ChatHistoryAdapter(private val sessions: List<Triple<String, String, Long>>) :
    RecyclerView.Adapter<ChatHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.sessionTitle)
        val date: TextView = itemView.findViewById(R.id.sessionDate)
        val preview: TextView = itemView.findViewById(R.id.sessionPreview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_session, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val (userMsg, botReply, ts) = sessions[position]
        holder.title.text = if (userMsg.length > 40) userMsg.take(40) + "…" else userMsg
        holder.preview.text = botReply
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.date.text = sdf.format(Date(ts))
    }

    override fun getItemCount() = sessions.size
}
