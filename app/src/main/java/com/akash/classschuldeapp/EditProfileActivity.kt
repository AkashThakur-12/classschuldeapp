package com.akash.classschuldeapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import android.widget.EditText

class EditProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var selectedImageUri: Uri? = null
    private lateinit var profileImageView: ShapeableImageView
    private lateinit var nameEditText: EditText
    private lateinit var branchSpinner: Spinner
    private lateinit var semSpinner: Spinner

    private val branches = arrayOf("CS", "IT", "CSB", "CSAI")
    private val semesters = arrayOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII")

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    Glide.with(this).load(it).circleCrop().into(profileImageView)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        profileImageView = findViewById(R.id.editProfileImage)
        nameEditText = findViewById(R.id.editName)
        branchSpinner = findViewById(R.id.editBranchSpinner)
        semSpinner = findViewById(R.id.editSemSpinner)

        branchSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, branches)
        semSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, semesters)

        // Pre-fill fields from SharedPrefs
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        nameEditText.setText(prefs.getString("name", ""))
        val savedBranch = prefs.getString("branch", "")
        val savedSem = prefs.getString("sem", "")
        if (branches.contains(savedBranch)) branchSpinner.setSelection(branches.indexOf(savedBranch))
        if (semesters.contains(savedSem)) semSpinner.setSelection(semesters.indexOf(savedSem))

        val imgUrl = prefs.getString("profile_image_url", null)
        if (!imgUrl.isNullOrEmpty()) {
            Glide.with(this).load(imgUrl).circleCrop().into(profileImageView)
        }

        findViewById<MaterialButton>(R.id.changePhotoButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        findViewById<MaterialButton>(R.id.saveProfileButton).setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val name = nameEditText.text.toString().trim()
        val branch = branchSpinner.selectedItem.toString()
        val sem = semSpinner.selectedItem.toString()
        val email = auth.currentUser?.email ?: return

        if (selectedImageUri != null) {
            uploadImageToCloudinary(selectedImageUri!!) { imageUrl ->
                saveToFirestore(email, name, branch, sem, imageUrl)
            }
        } else {
            val existing = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getString("profile_image_url", null)
            saveToFirestore(email, name, branch, sem, existing)
        }
    }

    private fun uploadImageToCloudinary(uri: Uri, onComplete: (String?) -> Unit) {
        val cloudName = BuildConfig.CLOUDINARY_CLOUD_NAME
        val apiKey = BuildConfig.CLOUDINARY_API_KEY
        val apiSecret = BuildConfig.CLOUDINARY_API_SECRET

        val inputStream = contentResolver.openInputStream(uri) ?: run {
            onComplete(null); return
        }
        val bytes = inputStream.readBytes()
        inputStream.close()

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val toSign = "timestamp=$timestamp$apiSecret"
        val signature = sha1Hex(toSign)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "profile.jpg",
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes))
            .addFormDataPart("api_key", apiKey)
            .addFormDataPart("timestamp", timestamp)
            .addFormDataPart("signature", signature)
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { Toast.makeText(this@EditProfileActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                onComplete(null)
            }
            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "{}")
                val url = json.optString("secure_url", null.toString()).takeIf { it != "null" }
                runOnUiThread { onComplete(url) }
            }
        })
    }

    private fun saveToFirestore(email: String, name: String, branch: String, sem: String, imageUrl: String?) {
        val data = hashMapOf(
            "name" to name,
            "branch" to branch,
            "sem" to sem,
            "profileImageUrl" to (imageUrl ?: "")
        )
        db.collection("users").document(email).set(data)
            .addOnSuccessListener {
                getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit()
                    .putString("name", name)
                    .putString("branch", branch)
                    .putString("sem", sem)
                    .putString("profile_image_url", imageUrl ?: "")
                    .apply()
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sha1Hex(input: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-1")
        val result = md.digest(input.toByteArray())
        return result.joinToString("") { "%02x".format(it) }
    }
}
