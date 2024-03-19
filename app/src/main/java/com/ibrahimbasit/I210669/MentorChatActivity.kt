package com.ibrahimbasit.I210669

import ChatMessageAdapter
import MentorChatMessageAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import java.util.UUID

class MentorChatActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatMessageAdapter: MentorChatMessageAdapter
    private val chatMessages: MutableList<ChatMessage> = mutableListOf()
    private lateinit var chatPersonRecyclerView : RecyclerView
    private var chatSessionId: String? = null
    var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_chat)
        checkAndRequestPermissions()
        chatSessionId = intent.getStringExtra("chatSessionId")
        val sessionName : String = intent.getStringExtra("chatSessionName") ?: "Chat"
        val chatPersonName: TextView = findViewById(R.id.nameHeading)
        chatPersonName.text = sessionName
        val cameraButton: View = findViewById(R.id.cameraButton)
        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val videoButton: View = findViewById(R.id.videoCallButton)
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            startActivity(intent)
        }
        val callButton: View = findViewById(R.id.callButton)
        callButton.setOnClickListener {
            val intent = Intent(this, CallScreenActivity::class.java)
            startActivity(intent)
        }

        val backButton: View = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        // Setup RecyclerView and Adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true // Start filling from the bottom
        chatPersonRecyclerView = findViewById(R.id.chatRecyclerView)
        chatPersonRecyclerView.layoutManager = layoutManager

        // Initialize and set the adapter
        chatMessageAdapter =
            FirebaseAuth.getInstance().currentUser?.let {
                MentorChatMessageAdapter(
                    it.uid,
                    chatMessages
                )
            }!!
        chatPersonRecyclerView.adapter = chatMessageAdapter

        databaseReference =
            chatSessionId?.let {
                FirebaseDatabase.getInstance().getReference("Messages").child(it)
            }!!

        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    chatMessages.add(it)
                    chatMessageAdapter.notifyItemInserted(chatMessages.size - 1)
                    chatPersonRecyclerView.scrollToPosition(chatMessages.size - 1)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle changes if needed
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle message removal if applicable
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Handle message moves if needed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
            }
        })


        val sendButton: Button = findViewById(R.id.sendButton)
        val messageBox: EditText = findViewById(R.id.myEditText)
        sendButton.setOnClickListener {
            val messageText = messageBox.text.toString().trim()
            if (messageText.isNotEmpty()) {
                // First, disable the send button to prevent multiple sends
                sendButton.isEnabled = false

                // Get mentor id from chat session id
                val ref =
                    FirebaseDatabase.getInstance().getReference("Chats").child(chatSessionId!!)
                ref.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val data = task.result?.getValue(ChatSession::class.java)
                        val receiverId =
                            data?.userId // Assuming this is the correct field for receiver ID

                        if (receiverId != null) {
                            // Prepare the ChatMessage object
                            val messageId = databaseReference.push().key
                            val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            val timestamp = System.currentTimeMillis()
                            val chatMessage = ChatMessage(
                                messageId!!,
                                senderId,
                                receiverId,
                                messageText,
                                timestamp,
                                "text",
                                null,
                                isEdited = false,
                                isDeleted = false
                            )

                            // Push the message to Firebase
                            messageId.let {
                                databaseReference.child(it).setValue(chatMessage)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Re-enable the send button
                                            sendButton.isEnabled = true
                                            Toast.makeText(
                                                this,
                                                "Message sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Message sent successfully
                                            messageBox.setText("") // Clear the message box
                                        } else {
                                            // Handle failure
                                            sendButton.isEnabled = true
                                            Toast.makeText(
                                                this,
                                                "Failed to send message",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            sendButton.isEnabled = true
                            Toast.makeText(this, "Receiver ID not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        sendButton.isEnabled = true
                        Toast.makeText(
                            this,
                            "Failed to retrieve chat session",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        val micButton : Button = findViewById(R.id.micButton)

        micButton.setOnTouchListener {

                v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRecording()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    stopRecording()
                    true
                }
                else -> false
            }

        }
    }


    // Example method to add a new message
    private fun startRecording() {
        audioFilePath = this.externalCacheDir?.absolutePath + "/audiorecordtest.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("ChatPersonFragment", "prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
        audioFilePath?.let { uploadAudioFile(it) }
    }

    private fun uploadAudioFile(filePath: String) {
        val fileId = UUID.randomUUID().toString()

        val file = Uri.fromFile(File(filePath))
        val storageRef = FirebaseStorage.getInstance().reference.child("audio/${fileId}")
        val uploadTask = storageRef.putFile(file)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                sendMessageWithAudio(uri.toString())
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

    private fun sendMessageWithAudio(audioUrl: String) {
        val ref =
            FirebaseDatabase.getInstance().getReference("Chats").child(chatSessionId!!)
        ref.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val data = task.result?.getValue(ChatSession::class.java)
                val receiverId =
                    data?.mentorId // Assuming this is the correct field for receiver ID

                if (receiverId != null) {
                    // Prepare the ChatMessage object
                    val messageId = databaseReference.push().key
                    val senderId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val timestamp = System.currentTimeMillis()
                    val chatMessage = ChatMessage(
                        messageId!!,
                        senderId,
                        receiverId,
                        "Voice note",
                        timestamp,
                        "voice",
                        audioUrl,
                        isEdited = false,
                        isDeleted = false
                    )

                    // Push the message to Firebase
                    messageId.let {
                        databaseReference.child(it).setValue(chatMessage)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Re-enable the send button
                                    Toast.makeText(
                                        this,
                                        "Message sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Message sent successfully
                                } else {
                                    // Handle failure
                                    Toast.makeText(
                                        this,
                                        "Failed to send message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Receiver ID not found", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Failed to retrieve chat session",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                ChatPersonFragment.REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
        // If permission is already granted, you might want to start some initialization here
    }
}