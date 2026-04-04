package com.akash.classschuldeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.akash.classschuldeapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val etemail = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString()?.trim() ?: ""

            if (etemail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val branchRegex = Regex("^(lcs|lit|lcb|lci)(\\d{4})\\d+@iiitl\\.ac\\.in\$")
            if (!branchRegex.matches(etemail.lowercase())) {
                Toast.makeText(this, "You must use a valid IIITL domain email.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(etemail, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("isRegistered", true).apply()
                        parseAndSaveUserDetails(etemail)

                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
        binding.tvLoginNow.setOnClickListener {
            val intent= Intent(this, loginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchAndSaveProfile(email: String) {
        db.collection("users").document(email).get()
            .addOnSuccessListener { doc ->
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                val edit = prefs.edit()
                if (doc.exists()) {
                    edit.putString("name", doc.getString("name"))
                    edit.putString("branch", doc.getString("branch"))
                    edit.putString("sem", doc.getString("sem"))
                    edit.putString("profile_image_url", doc.getString("profileImageUrl"))
                } else {
                    // Fallback to regex parsing for first-time login
                    parseAndSaveUserDetails(email)
                }
                edit.apply()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                parseAndSaveUserDetails(email)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    private fun parseAndSaveUserDetails(email: String) {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        // e.g., lcs2024008@iiitl.ac.in
        val branchRegex = Regex("^(lcs|lit|lcb|lci)(\\d{4})\\d+@iiitl\\.ac\\.in\$")
        val match = branchRegex.find(email.lowercase())

        if (match != null) {
            val prefix = match.groupValues[1]
            val branchCode = when (prefix) {
                "lcs" -> "CS"
                "lit" -> "IT"
                "lcb" -> "CSB"
                "lci" -> "CSAI"
                else -> "CS"
            }
            val admissionYear = match.groupValues[2].toInt()
            
            val currentYear = 2026 // Based on current setup
            val semNum = (currentYear - admissionYear) * 2

            val romanSem = when {
                semNum <= 1 -> "I"
                semNum == 2 -> "II"
                semNum == 3 -> "III"
                semNum == 4 -> "IV"
                semNum == 5 -> "V"
                semNum == 6 -> "VI"
                semNum == 7 -> "VII"
                else -> "VIII"
            }

            val profileData = mapOf(
                "branch" to branchCode,
                "sem" to romanSem,
                "name" to "Student",
                "profileImageUrl" to ""
            )

            prefs.edit()
                .putString("branch", branchCode)
                .putString("sem", romanSem)
                .apply()

            // Sync with Firestore
            db.collection("users").document(email)
                .set(profileData)
        }
    }
}
