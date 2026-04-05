package com.akash.classschuldeapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ChatLocalStorage {

    private const val PREF_NAME = "ChatHistoryPrefs"
    private const val KEY_SESSIONS = "sessions_list"
    private const val KEY_MESSAGES_PREFIX = "messages_"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    fun saveSession(context: Context, sessionItem: ChatSessionItem) {
        val prefs = getPrefs(context)
        val sessions = getSessions(context).toMutableList()
        val index = sessions.indexOfFirst { it.sessionId == sessionItem.sessionId }
        if (index != -1) {
            sessions[index] = sessionItem
        } else {
            sessions.add(sessionItem)
        }
        prefs.edit().putString(KEY_SESSIONS, gson.toJson(sessions)).apply()
    }

    fun getSessions(context: Context): List<ChatSessionItem> {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_SESSIONS, null) ?: return emptyList()
        val type = object : TypeToken<List<ChatSessionItem>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveMessage(context: Context, sessionId: String, message: ChatMessage) {
        val prefs = getPrefs(context)
        val messages = getMessages(context, sessionId).toMutableList()
        messages.add(message)
        prefs.edit().putString(KEY_MESSAGES_PREFIX + sessionId, gson.toJson(messages)).apply()
    }

    fun getMessages(context: Context, sessionId: String): List<ChatMessage> {
        val prefs = getPrefs(context)
        val json = prefs.getString(KEY_MESSAGES_PREFIX + sessionId, null) ?: return emptyList()
        val type = object : TypeToken<List<ChatMessage>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
