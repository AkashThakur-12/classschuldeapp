package com.akash.classschuldeapp

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akash.classschuldeapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        // SAVE REGISTER FLAG
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isRegistered", true).apply()



        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val etemail = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etconfirmpassword.text.toString().trim()



            if (etemail.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etemail).matches()) {
                binding.etEmail.error = "Enter a valid email address"
                binding.etEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                binding.etconfirmpassword.error = "Passwords do not match"
                binding.etconfirmpassword.requestFocus()
                return@setOnClickListener
            }


            auth.createUserWithEmailAndPassword(etemail, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        prefs.edit().putBoolean("isRegistered", true).apply()

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




    }
