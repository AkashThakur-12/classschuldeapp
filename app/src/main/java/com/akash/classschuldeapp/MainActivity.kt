package com.akash.classschuldeapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navChatbot: LinearLayout
    private lateinit var navHistory: LinearLayout
    private lateinit var navProfile: LinearLayout

    private lateinit var iconHome: ImageView
    private lateinit var iconChatbot: ImageView
    private lateinit var iconHistory: ImageView
    private lateinit var iconProfile: ImageView

    private lateinit var textHome: TextView
    private lateinit var textChatbot: TextView
    private lateinit var textHistory: TextView
    private lateinit var textProfile: TextView

    private val TEAL = "#44DDC1"
    private val GREY = "#948DA2"
    private val ACTIVE_BG = "#1E3F35"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        navHome = findViewById(R.id.nav_item_home)
        navChatbot = findViewById(R.id.nav_item_chatbot)
        navHistory = findViewById(R.id.nav_item_history)
        navProfile = findViewById(R.id.nav_item_profile)

        iconHome = findViewById(R.id.nav_icon_home)
        iconChatbot = findViewById(R.id.nav_icon_chatbot)
        iconHistory = findViewById(R.id.nav_icon_history)
        iconProfile = findViewById(R.id.nav_icon_profile)

        textHome = findViewById(R.id.nav_text_home)
        textChatbot = findViewById(R.id.nav_text_chatbot)
        textHistory = findViewById(R.id.nav_text_history)
        textProfile = findViewById(R.id.nav_text_profile)

        // Default to Home
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            setActiveNav(navHome, iconHome, textHome)
        }

        navHome.setOnClickListener {
            loadFragment(HomeFragment())
            setActiveNav(navHome, iconHome, textHome)
        }

        navChatbot.setOnClickListener {
            loadFragment(ChatbotFragment.newInstance())
            setActiveNav(navChatbot, iconChatbot, textChatbot)
        }

        navHistory.setOnClickListener {
            loadFragment(ChatHistoryFragment())
            setActiveNav(navHistory, iconHistory, textHistory)
        }

        navProfile.setOnClickListener {
            loadFragment(ProfileFragment())
            setActiveNav(navProfile, iconProfile, textProfile)
        }
    }

    private fun setActiveNav(activeLayout: LinearLayout, activeIcon: ImageView, activeText: TextView) {
        // Reset all
        val allLayouts = listOf(navHome, navChatbot, navHistory, navProfile)
        val allIcons = listOf(iconHome, iconChatbot, iconHistory, iconProfile)
        val allTexts = listOf(textHome, textChatbot, textHistory, textProfile)

        allLayouts.forEach { it.background = null }
        allIcons.forEach { it.setColorFilter(Color.parseColor(GREY)) }
        allTexts.forEach { it.setTextColor(Color.parseColor(GREY)) }

        // Activate selected
        activeLayout.setBackgroundResource(R.drawable.nav_active_bg)
        activeIcon.setColorFilter(Color.parseColor(TEAL))
        activeText.setTextColor(Color.parseColor(TEAL))
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}
