package com.ibrahimbasit.I210669

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class MentorSignUp : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val REQUEST_CODE_IMAGE_PICK = 1234 // Choose a unique integer.

    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null // Holds the selected image URI
    private  lateinit var progressBar: ProgressBar



    // UI Components
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var roleEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var uploadButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_sign_up)

        val backButton = findViewById<View>(R.id.backButton)

        backButton.setOnClickListener {
            finish()
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Mentors")

        // Bind UI components
        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        priceEditText = findViewById(R.id.priceEditText)
        roleEditText = findViewById(R.id.roleEditText)
        statusSpinner = findViewById(R.id.statusTextInputLayout)
        uploadButton = findViewById(R.id.uploadButton)
        progressBar = findViewById(R.id.addMentorProgressBar)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)

        val loginMentor : TextView = findViewById(R.id.loginMentor)

        val uploadPicContainer : View = findViewById(R.id.picUploadContainer)

        uploadPicContainer.setOnClickListener {
            pickImageFromGallery()
        }

        val countries = arrayOf("Available", "Not Available")
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, countries)

        statusSpinner = findViewById(R.id.statusTextInputLayout)
        statusSpinner.adapter = adapter
        statusSpinner.setSelection(0)

        uploadButton.setOnClickListener {
            if (imageUri != null) {
                showLoading(true)

                registerEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
            }


        }

        loginMentor.setOnClickListener {
            val intent = Intent(this, MentorLogin::class.java)
            startActivity(intent)
        }

    }


    fun registerEmailAndPassword(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadMentor()
                }
            }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            val text = findViewById<TextView>(R.id.uploadPicText)
            text?.text = "Image selected"

        }

    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun uploadMentor() {
        // Assuming you have implemented methods to get these values from your UI
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val price = priceEditText.text.toString().toFloatOrNull() ?: 0f
        val role = roleEditText.text.toString()
        val availability = statusSpinner.selectedItem.toString() == "Available"

        // Create a new Mentor object
        val mentor = FirebaseAuth.getInstance().currentUser?.let { Mentor(it.uid, name, role, availability, "", price, description, 0f) }


        // First, add the mentor to the database
        if (mentor != null) {
            val pref = applicationContext.getSharedPreferences("MyPref", 0)
            val token = pref.getString("fcmToken", null)
            mentor.fcmToken = token
            databaseReference.child(mentor.mentorId).setValue(mentor).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // If mentor data is successfully added, upload the image next
                    imageUri?.let { uri ->
                        uploadImageToFirebaseStorage(mentor.mentorId, uri)
                    }
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Failed to add mentor. Please try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun uploadImageToFirebaseStorage(mentorId: String, imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().getReference("mentor_profile_images/$mentorId.jpg")
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                // After image upload, update the mentor's profilePictureUrl
                updateMentorProfilePictureUrl(mentorId, downloadUri.toString())
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(this, "Image upload failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMentorProfilePictureUrl(mentorId: String, profilePictureUrl: String) {
        databaseReference.child(mentorId).child("profilePictureUrl").setValue(profilePictureUrl).addOnSuccessListener {
            showLoading(false)
            val intent = Intent(this, MentorMainActivity::class.java)
            startActivity(intent)
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(this, "Failed to update mentor's profile picture.", Toast.LENGTH_SHORT).show()
        }
    }
}