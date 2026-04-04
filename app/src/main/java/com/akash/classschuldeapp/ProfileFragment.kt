package com.akash.classschuldeapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile(view)

        view.findViewById<MaterialButton>(R.id.editProfileButton).setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            auth.signOut()
            requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .edit().clear().apply()
            startActivity(Intent(requireContext(), loginActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadProfile(it) }
    }

    private fun loadProfile(view: View) {
        val prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = auth.currentUser?.email ?: ""

        // Populate from SharedPrefs immediately
        view.findViewById<TextView>(R.id.profileName).text = prefs.getString("name", "Student") ?: "Student"
        view.findViewById<TextView>(R.id.profileEmail).text = email
        view.findViewById<TextView>(R.id.branchDetail).text = prefs.getString("branch", "—") ?: "—"
        view.findViewById<TextView>(R.id.semDetail).text = prefs.getString("sem", "—") ?: "—"

        val imageUrl = prefs.getString("profile_image_url", null)
        if (!imageUrl.isNullOrEmpty()) {
            val imageView = view.findViewById<ShapeableImageView>(R.id.profileImage)
            if (isAdded) {
                Glide.with(this).load(imageUrl).circleCrop()
                    .placeholder(R.drawable.profile_placeholder)
                    .into(imageView)
            }
        }

        // Refresh from Firestore in background
        if (email.isNotEmpty()) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { doc ->
                    if (!isAdded || view.parent == null) return@addOnSuccessListener
                    if (doc.exists()) {
                        val name = doc.getString("name") ?: prefs.getString("name", "Student") ?: "Student"
                        val branch = doc.getString("branch") ?: prefs.getString("branch", "—") ?: "—"
                        val sem = doc.getString("sem") ?: prefs.getString("sem", "—") ?: "—"
                        val imgUrl = doc.getString("profileImageUrl")

                        view.findViewById<TextView>(R.id.profileName).text = name
                        view.findViewById<TextView>(R.id.branchDetail).text = branch
                        view.findViewById<TextView>(R.id.semDetail).text = sem

                        // Save back to prefs
                        prefs.edit()
                            .putString("name", name)
                            .putString("branch", branch)
                            .putString("sem", sem)
                            .apply()

                        if (!imgUrl.isNullOrEmpty() && isAdded) {
                            Glide.with(this).load(imgUrl).circleCrop()
                                .placeholder(R.drawable.profile_placeholder)
                                .into(view.findViewById(R.id.profileImage))
                        }
                    }
                }
        }
    }
}
