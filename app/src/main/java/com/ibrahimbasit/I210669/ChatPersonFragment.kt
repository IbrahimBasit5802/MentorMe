package com.ibrahimbasit.I210669

import ChatMessageAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException
import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.FileOutputStream
import java.util.UUID


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatPersonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatPersonFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private val chatMessages: MutableList<ChatMessage> = mutableListOf()
    private lateinit var chatPersonRecyclerView : RecyclerView
    private var chatSessionId: String? = null
    private var chatSessionName: String? = null
    var mediaRecorder: MediaRecorder? = null
    private var audioFilePath: String? = null



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            chatSessionId = it.getString(ARG_CHAT_SESSION_ID)
            chatSessionName = it.getString(ARG_CHAT_SESSION_NAME)

        }

        checkAndRequestPermissions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        val sessionName : TextView = view.findViewById(R.id.nameHeading)
        sessionName.text = chatSessionName

        val videoButton: View = view.findViewById(R.id.videoCallButton)
        videoButton.setOnClickListener {
            val intent = Intent(activity, VideoCallActivity::class.java)
            startActivity(intent)
        }
        val callButton: View = view.findViewById(R.id.callButton)
        callButton.setOnClickListener {
            val intent = Intent(activity, CallScreenActivity::class.java)
            startActivity(intent)
        }

        val backButton: View = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            activity?.onBackPressed()
        }

        val micButton : Button = view.findViewById(R.id.micButton)

        // Setup RecyclerView and Adapter
        val layoutManager = LinearLayoutManager(context)
        layoutManager.stackFromEnd = true // Start filling from the bottom
        chatPersonRecyclerView = view.findViewById(R.id.chatRecyclerView)
        chatPersonRecyclerView.layoutManager = layoutManager

        // Initialize and set the adapter
        chatMessageAdapter =
            FirebaseAuth.getInstance().currentUser?.let {
                ChatMessageAdapter(
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

        // find mentor id from chatSession id


        val sendButton: Button = view.findViewById(R.id.sendButton)
        val messageBox: EditText = view.findViewById(R.id.myEditText)
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
                                                context,
                                                "Message sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            // Message sent successfully
                                            messageBox.setText("") // Clear the message box
                                        } else {
                                            // Handle failure
                                            sendButton.isEnabled = true
                                            Toast.makeText(
                                                context,
                                                "Failed to send message",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            }
                        } else {
                            sendButton.isEnabled = true
                            Toast.makeText(context, "Receiver ID not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        sendButton.isEnabled = true
                        Toast.makeText(
                            context,
                            "Failed to retrieve chat session",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

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

        val cameraButton: Button = view.findViewById(R.id.cameraButton)


        cameraButton.setOnClickListener {
            Toast.makeText(context, "Hoja bhaye", Toast.LENGTH_SHORT).show()
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Convert bitmap to Uri (consider saving the bitmap to a file and getting Uri from file)
            // For demonstration, assuming you have a method to get Uri from Bitmap
            val imageUri = getImageUriFromBitmap(requireContext(), imageBitmap)
            uploadImageToFirebase(imageUri)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileId = UUID.randomUUID().toString()
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$fileId")
        val uploadTask = storageRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                sendMessageWithImage(uri.toString())
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }

    private fun sendMessageWithImage(imageUrl: String) {
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
                        "image",
                        timestamp,
                        "image",
                        imageUrl,
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
                                        context,
                                        "Message sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Message sent successfully
                                } else {
                                    // Handle failure
                                    Toast.makeText(
                                        context,
                                        "Failed to send message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Receiver ID not found", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Failed to retrieve chat session",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Utility function to convert Bitmap to Uri
    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val tempDir = File(context.externalCacheDir, "temp_images")
        if (!tempDir.exists()) tempDir.mkdir()
        val tempFile = File(tempDir, "temp_image.jpg")
        try {
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.fromFile(tempFile)
    }





    // Example method to add a new message
    private fun startRecording() {
        audioFilePath = requireContext().externalCacheDir?.absolutePath + "/audiorecordtest.3gp"

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
                                        context,
                                        "Message sent",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Message sent successfully
                                } else {
                                    // Handle failure
                                    Toast.makeText(
                                        context,
                                        "Failed to send message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, "Receiver ID not found", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Failed to retrieve chat session",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
        // If permission is already granted, you might want to start some initialization here
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_person, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatPersonFragment.
         */
        // TODO: Rename and change types and number of parameters

        private const val ARG_CHAT_SESSION_ID = "chatSessionId"
        private const val ARG_CHAT_SESSION_NAME = "chatSessionName"
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_CAMERA_PERMISSION = 101
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200


        @JvmStatic
        fun newInstance(chatSessionId: String, chatSessionName: String) =
            ChatPersonFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHAT_SESSION_ID, chatSessionId)
                    putString(ARG_CHAT_SESSION_NAME, chatSessionName)

                }
            }

    }
}