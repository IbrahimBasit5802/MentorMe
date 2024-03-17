package com.ibrahimbasit.I210669//package com.ibrahimbasit.I210669
//
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.UUID


class SelectSessionActivity : AppCompatActivity() {

    private var monthHeader: TextView? = null
    private var calendarGrid: GridLayout? = null
    private lateinit var buttonsList: List<Button>
    private var selectedButton: Button? = null
    private lateinit var mentorViewModel: MentorViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_session)
        val mentor = intent.getSerializableExtra("mentor_data") as? Mentor


        mentorViewModel = ViewModelProvider(this).get(MentorViewModel::class.java)
        mentorViewModel.loadMentor(mentor!!.mentorId)


        val backButton : View = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        var mentorImage : ImageView = findViewById(R.id.mentor_image)
        val mentorName : TextView = findViewById(R.id.name)
        val mentorRating : TextView = findViewById(R.id.ratingText)
        val mentorPrice : TextView = findViewById(R.id.rate_text)


        val timeSlotButton : Button = findViewById(R.id.timeSlot1)
        val timeSlotButton2 : Button = findViewById(R.id.timeSlot2)
        val timeSlotButton3 : Button = findViewById(R.id.timeSlot3)
        val timeSlotButton4 : Button = findViewById(R.id.timeSlot4)


        if (mentor != null) {
            mentorName.text = mentor.name
            mentorRating.text = mentor.rating.toString()
            mentorPrice.text = "$" + mentor.price.toString() + "/Session"
            // load mentor image using picasso
            Picasso.get().load(mentor.profilePictureUrl).fit().into(mentorImage)
        }

        mentorViewModel.mentor.observe(this) {
            if (it != null) {
                mentorName.text = it.name
                mentorRating.text = it.rating.toString()
                mentorPrice.text = "$" + it.price.toString() + "/Session"
                // load mentor image using picasso
            }
        }


        setSelectedButton(timeSlotButton2)
        buttonsList = (listOf(timeSlotButton, timeSlotButton2, timeSlotButton3, timeSlotButton4))
        buttonsList.forEach { button ->
            button.setOnClickListener { clickedButton ->
                setSelectedButton(clickedButton as Button)
            }
        }

        val chatButton : Button = findViewById(R.id.chatButton)
        chatButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                // Add extra to indicate the target fragment
                putExtra("navigateTo", "ChatPersonFragment")
            }
            startActivity(intent)
        }

        val callButton : Button = findViewById(R.id.phoneButton)

        callButton.setOnClickListener {
            val intent = Intent(this, CallScreenActivity::class.java)

            startActivity(intent)
        }

        val videoButton : Button = findViewById(R.id.videoButton)

        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)

            startActivity(intent)
        }

        val bookButton : Button = findViewById(R.id.bookSessionButton)
        bookButton.setOnClickListener {
            // Assume you have the current user's ID and the mentor's ID
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            // get mentor id from mentor profilepic url snapshot.key

            val mentorId = mentor?.mentorId

            // Create a chat session object
            val chatSessionId = UUID.randomUUID().toString()
            val newChatSession = mentor?.let { it1 ->
                ChatSession(
                    id = chatSessionId,
                    name = it1.name,
                    lastMessage = "You have booked a session with ${it1.name}!",
                    newMessagesCount = 1,
                    profilePictureUrl = mentor.profilePictureUrl
                )
            }

            // Firebase database references
            val chatSessionsRef = FirebaseDatabase.getInstance().getReference("Chats")
            val usersRef = FirebaseDatabase.getInstance().getReference("Users")
            val mentorsRef = FirebaseDatabase.getInstance().getReference("Mentors")

            // Update the database
            chatSessionsRef.child(chatSessionId).setValue(newChatSession).addOnSuccessListener {
                // Add chat session to user
                if (currentUserId != null) {
                    usersRef.child(currentUserId).child("chatSessions").child(chatSessionId).setValue(false)
                }
                // Add chat session to mentor
                if (mentorId != null) {
                    mentorsRef.child(mentorId).child("chatSessions").child(chatSessionId).setValue(false)
                }
                // Navigate to the chat

                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to book session", Toast.LENGTH_SHORT).show()
            }

            finish()
        }





    }

    private fun setSelectedButton(button: Button) {
        // Change background and text color of the clicked button
        selectedButton?.let {
            it.backgroundTintList = ContextCompat.getColorStateList(this, R.color.timeSloeUnselectedColor)
            it.setTextColor(ContextCompat.getColor(this, R.color.secondaryTextColor))
        }

        button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.timeSlotColor)
        button.setTextColor(ContextCompat.getColor(this, R.color.primaryTextColor))

        // Keep reference to the selected button
        selectedButton = button
    }
}