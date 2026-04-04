package com.akash.classschuldeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.akash.classschuldeapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class loginActivity : AppCompatActivity() {
    
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Email / Password Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString()?.trim() ?: ""
            val password = binding.etPassword.text?.toString()?.trim() ?: ""

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val iiitlRegex = Regex("^(lcs|lit|lcb|lci)(\\d{4})\\d+@iiitl\\.ac\\.in\$")
            if (!iiitlRegex.matches(email.lowercase())) {
                Toast.makeText(this, "You must use a valid IIITL domain email", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        fetchAndSaveProfile(email)
                    } else {
                        Toast.makeText(this, it.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Navigate to Register
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Google Sign-In
        binding.btnGoogleSignIn.setOnClickListener {
            // Sign out first to force account picker to show
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            }
        }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            } else {
                Toast.makeText(this, "Google sign-in cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    val email = account.email ?: ""
                    parseAndSaveUserDetails(email)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, it.exception?.message ?: "Firebase auth failed", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, task.exception?.message ?: "Google Sign-In failed", Toast.LENGTH_SHORT).show()
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
            val currentYear = 2026
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

            prefs.edit()
                .putString("branch", branchCode)
                .putString("sem", romanSem)
                .apply()
        }
    }
}
