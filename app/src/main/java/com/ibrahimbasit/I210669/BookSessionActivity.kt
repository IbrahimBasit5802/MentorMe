package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso

class BookSessionActivity : AppCompatActivity() {

    private lateinit var mentorViewModel: MentorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)

        mentorViewModel = ViewModelProvider(this).get(MentorViewModel::class.java)

        val mentorName = findViewById<TextView>(R.id.mentor_name)
        val mentorImage = findViewById<ImageView>(R.id.mentor_image)
        val mentorRole = findViewById<TextView>(R.id.mentor_role)
        val rating = findViewById<TextView>(R.id.rating)
        val description = findViewById<TextView>(R.id.mentor_description)
        val mentor = intent.getSerializableExtra("mentor_data") as? Mentor

        val booksessionButton : Button = findViewById(R.id.bookButton)

        mentorViewModel.loadMentor(mentor!!.mentorId)

        booksessionButton.setOnClickListener {
            val intent = Intent(this, SelectSessionActivity::class.java)
            intent.putExtra("mentor_data", mentor)
            startActivity(intent)
        }

        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }



        val dropReview : Button = findViewById(R.id.review_button)
        dropReview.setOnClickListener {
            val intent = Intent(this, FeedbackActivity::class.java)
            intent.putExtra("mentor_data", mentor)
            startActivity(intent)
        }

        val joinCommunity : Button = findViewById(R.id.community_button)
        joinCommunity.setOnClickListener {
            val intent = Intent(this, CommunityChatFragment::class.java)
            startActivity(intent)
        }

        if (mentor != null) {
            mentorName.text = mentor.name
            mentorRole.text = mentor.role
            rating.text = mentor.rating.toString()
            description.text = mentor.description
            // load mentor image using picasso
            Picasso.get().load(mentor.profilePictureUrl).fit().into(mentorImage)
        }

        mentorViewModel.mentor.observe(this) { mentor ->
            if (mentor != null) {
                mentorName.text = mentor.name
                mentorRole.text = mentor.role
                rating.text = mentor.rating.toString()
                description.text = mentor.description
                // load mentor image using picasso
            }
        }
    }
}