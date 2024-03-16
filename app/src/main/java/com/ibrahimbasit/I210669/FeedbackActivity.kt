package com.ibrahimbasit.I210669

import UserViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class FeedbackActivity : AppCompatActivity() {
    private  lateinit var progressBar: ProgressBar
    private lateinit var userViewModel: UserViewModel
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)


        val mentorName = findViewById<TextView>(R.id.mentor_name)
        val mentorImage = findViewById<ImageView>(R.id.mentor_image)
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        progressBar = findViewById(R.id.progressBar)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


        ratingBar.numStars = 5
        ratingBar.stepSize = 0.5f


        val mentor = intent.getSerializableExtra("mentor_data") as? Mentor
        if (mentor != null) {
            mentorName.text = mentor.name
            // load mentor image using picasso
            Picasso.get().load(mentor.profilePictureUrl).fit().into(mentorImage)
        }
        val submitFeedback : Button = findViewById(R.id.feedbackButton)
        submitFeedback.setOnClickListener {
            val reviewText = findViewById<EditText>(R.id.feedback).text.toString()
            val rating = ratingBar.rating
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // get mentor id from snapshot.key of node
            // query mentor snapshot from profiulepicture url
            val mentorRef = Firebase.database.getReference("Mentors")
            if (mentor != null) {
                mentorRef.orderByChild("profilePictureUrl").equalTo(mentor.profilePictureUrl).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { child ->
                            child.key?.let { mentorId ->
                                submitFeedback(rating, reviewText, mentorId, userId)
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        showLoading(false)
                        Toast.makeText(this@FeedbackActivity, "Error fetching mentor", Toast.LENGTH_SHORT).show()
                        // Handle possible errors.
                    }
                })
            }

        }

        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun submitFeedback(rating: Float, reviewText: String, mentorId: String, userId: String) {
        val review = Review(rating, reviewText, mentorId, userId)
        val dbRef = Firebase.database.getReference("Reviews")
        dbRef.push().setValue(review).addOnSuccessListener {
            Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show() // Ensure 'context' is valid in your fragment or activity

            // Fetch all reviews for the mentor to calculate the average rating
            dbRef.orderByChild("mentorId").equalTo(mentorId).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalRating = 0.0f
                    var reviewCount = 0

                    snapshot.children.forEach { child ->
                        val review = child.getValue(Review::class.java)
                        totalRating += review?.rating ?: 0.0f
                        reviewCount++
                    }

                    if (reviewCount > 0) {
                        val averageRating = totalRating / reviewCount
                        updateMentorRating(mentorId, averageRating)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    showLoading(false)
                    Toast.makeText(this@FeedbackActivity, "Error fetching reviews", Toast.LENGTH_SHORT).show()
                    // Handle possible errors.
                }
            })
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun updateMentorRating(mentorId: String, newRating: Float) {
        val mentorRef = Firebase.database.getReference("Mentors").child(mentorId)
        mentorRef.child("rating").setValue(newRating).addOnSuccessListener {
            showLoading(false)
            Toast.makeText(this, "Mentor rating updated successfully", Toast.LENGTH_SHORT).show()
        }
    }

}