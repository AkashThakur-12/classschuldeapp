package com.akash.classschuldeapp

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.akash.classschuldeapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.concurrent.Executor


class loginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLogin.setOnClickListener {
            val email = binding.etemail.text.toString()
            val password = binding.etpassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty field is not allowed", Toast.LENGTH_SHORT).show()
            }
        }


        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        findViewById<ImageView>(R.id.google).setOnClickListener {
            signInGoogle()
        }


        setupBiometric()
    }



    private fun setupBiometric() {
        val biometricManager = BiometricManager.from(this)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                binding.loginfingerprintButton.setOnClickListener {
                    authenticateUser()
                }
            }
            else -> {
                Toast.makeText(this, "Biometric not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateUser() {
        val executor: Executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(this@loginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@loginActivity, MainActivity::class.java))
                    finish()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@loginActivity, errString, Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Login")
            .setSubtitle("Use fingerprint to continue")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }



    private fun signInGoogle() {
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
        }
    }
}
