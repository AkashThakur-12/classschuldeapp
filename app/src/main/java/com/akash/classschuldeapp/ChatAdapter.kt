package com.akash.classschuldeapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val botLayout: View = itemView.findViewById(R.id.botLayout)
        val botMessage: TextView = itemView.findViewById(R.id.botMessage)
        val userLayout: View = itemView.findViewById(R.id.userLayout)
        val userMessage: TextView = itemView.findViewById(R.id.userMessage)
        val userAttachment: ImageView = itemView.findViewById(R.id.userAttachment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        if (message.isUser) {
            holder.userLayout.visibility = View.VISIBLE
            holder.botLayout.visibility = View.GONE
            holder.userMessage.text = message.text
            
            if (!message.imageUri.isNullOrEmpty()) {
                holder.userAttachment.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                     .load(message.imageUri)
                     .into(holder.userAttachment)
            } else {
                holder.userAttachment.visibility = View.GONE
            }
        } else {
            holder.botLayout.visibility = View.VISIBLE
            holder.userLayout.visibility = View.GONE
            holder.botMessage.text = message.text
            holder.userAttachment.visibility = View.GONE
        }
    }

    override fun getItemCount() = messages.size
}
