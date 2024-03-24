package com.ibrahimbasit.I210669

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.AgoraEngine
import com.ibrahimbasit.I210669.Mentor
import com.ibrahimbasit.I210669.R
import com.squareup.picasso.Picasso
import io.agora.rtc.RtcEngine

class MentorCallActivity : AppCompatActivity() {
    private var isMuted = false
    private lateinit var rtcEngine: RtcEngine
    private lateinit var channelName : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_call)
        val userId = intent.getStringExtra("userId")
        channelName = userId.toString() + FirebaseAuth.getInstance().currentUser?.uid
        rtcEngine = AgoraEngine.getInstance()!!

        val profilePic : ImageView = findViewById(R.id.profilePic)
        val name : TextView = findViewById(R.id.name)

        // get mentor name and profile picture from mentorId

        if (userId != null) {
            Firebase.database.getReference("Users").child(userId).get().addOnSuccessListener {
                name.text = it.getValue(Mentor::class.java)?.name
                Picasso.get().load(it.getValue(Mentor::class.java)?.profilePictureUrl).into(profilePic)
            }
        }

        // get user name and profile picture from userId

        joinChannel()
        setupUI()


        val closeButton : View = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            leaveChannel()
            finish()
        }
    }

    private fun setupUI() {
        val micButton: FrameLayout = findViewById(R.id.micButton)
        val closeButton: FrameLayout = findViewById(R.id.closeButton)

        micButton.setOnClickListener {
            isMuted = !isMuted
            rtcEngine.muteLocalAudioStream(isMuted)
        }



        closeButton.setOnClickListener {
            leaveChannel()
            finish()
        }
    }



    private fun leaveChannel() {
        rtcEngine.leaveChannel()
    }

    override fun onDestroy() {
        super.onDestroy()

        leaveChannel()
        AgoraEngine.destroy()
    }

    private fun joinChannel() {
        // Replace "yourChannelName" with the actual channel name.
        // Replace null with a token if your project has enabled the App Certificate.
        rtcEngine.joinChannel(null, channelName, "Extra Optional Data", 0)
        Toast.makeText(this, "Joined channel $channelName", Toast.LENGTH_LONG).show()

    }

}