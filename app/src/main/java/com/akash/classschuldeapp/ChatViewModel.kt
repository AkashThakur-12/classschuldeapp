package com.akash.classschuldeapp

import androidx.lifecycle.ViewModel

class ChatViewModel : ViewModel() {
    val messages = mutableListOf<ChatMessage>()
    var currentSessionId: String = java.util.UUID.randomUUID().toString()
}
