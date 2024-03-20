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
import android.view.GestureDetector
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
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

        val swipeToDeleteCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We don't want move functionality
            }



            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // Implement deletion logic here
                val message = chatMessageAdapter.getItemAtPosition(position)
                if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
                    // Calculate the time difference between the current time and the message's timestamp
                    val currentTime = System.currentTimeMillis()
                    val messageTime = message.timestamp
                    val timeDifference = currentTime - messageTime

                    // Check if the message was sent within the last 5 minutes
                    val isWithin5Minutes = timeDifference <= (5 * 60 * 1000)

                    if (isWithin5Minutes) {
                        // If the message is within 5 minutes, delete it
                        deleteMessage(position, message)
                    } else {
                        // Handle message deletion restriction here (e.g., show a toast message)
                        Toast.makeText(context, "You can only delete messages sent within the last 5 minutes", Toast.LENGTH_SHORT).show()
                        chatMessageAdapter.notifyItemChanged(position) // Restore swiped item if it's not deleted
                    }
                } else {
                    chatMessageAdapter.notifyItemChanged(position) // Restore swiped item if it's not the user's message
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(chatPersonRecyclerView)

        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                handleLongPress(e)
            }

        })

        chatPersonRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                gestureDetector.onTouchEvent(e)
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })


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
                val updatedMessage = snapshot.getValue(ChatMessage::class.java)
                updatedMessage?.let { msg ->
                    // Find the message in the list by its ID
                    val index = chatMessages.indexOfFirst { it.id == msg.id }
                    if (index != -1) {
                        // Replace the old message with the updated one
                        chatMessages[index] = msg
                        // Notify the adapter of the change
                        chatMessageAdapter.notifyItemChanged(index)
                    }
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

                val message = snapshot.getValue(ChatMessage::class.java)
                message?.let {
                    val index = chatMessages.indexOfFirst { it.id == message.id }
                    if (index != -1) {
                        chatMessages.removeAt(index)
                        chatMessageAdapter.notifyItemRemoved(index)
                    }
                }
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
//            val intent = Intent(context, CameraXActivity::class.java)
//            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        val imageuploadButton = view.findViewById<Button>(R.id.imageUploadButton)

        imageuploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        val attachFileButton = view.findViewById<Button>(R.id.attachFileButton)
        attachFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*" // Allows any file type. Adjust if needed.
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, REQUEST_CODE_FILE_PICK)
        }
    }

    private fun handleLongPress(event: MotionEvent) {
        val childView = chatPersonRecyclerView.findChildViewUnder(event.x, event.y)
        if (childView != null) {
            val position = chatPersonRecyclerView.getChildAdapterPosition(childView)
            // Ensure position is valid
            if (position != RecyclerView.NO_POSITION) {
                val message = chatMessageAdapter.getItemAtPosition(position)
                // Check if the message is a text message
                if (message.type == "text" && message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
                    // Calculate time difference to ensure it's within the last 5 minutes
                    val currentTime = System.currentTimeMillis()
                    val messageTime = message.timestamp
                    val timeDifference = currentTime - messageTime

                    if (timeDifference <= (5 * 60 * 1000)) {
                        // Only proceed with editing if it's a text message and sent within the last 5 minutes
                        editMessage(message, position)
                    } else {
                        // Optionally, inform the user that the message can't be edited
                        Toast.makeText(context, "You can only edit messages sent within the last 5 minutes", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


    private fun editMessage(message: ChatMessage, position: Int) {
        // Show a dialog to edit the message
        val editText = EditText(context).apply {
            setText(message.message)
        }

        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Edit Message")
                .setView(editText)
                .setPositiveButton("Update") { _, _ ->
                    val updatedMessageText = editText.text.toString()
                    if (updatedMessageText.isNotEmpty() && updatedMessageText != message.message) {
                        // Update the message in Firebase
                        FirebaseDatabase.getInstance().getReference("Messages")
                            .child(chatSessionId!!)
                            .child(message.id)
                            .child("message").setValue(updatedMessageText)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Message updated", Toast.LENGTH_SHORT).show()
                            }
                        // Optionally, mark the message as edited
                        FirebaseDatabase.getInstance().getReference("Messages")
                            .child(chatSessionId!!)
                            .child(message.id)
                            .child("isEdited").setValue(true)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    private fun deleteMessage(position: Int, message: ChatMessage) {
        // Delete from Firebase Database
        FirebaseDatabase.getInstance().getReference("Messages").child(chatSessionId!!).child(message.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
            }

        // If the message has a mediaUrl, delete the media from Firebase Storage
        message.mediaUrl?.let { mediaUrl ->
            val mediaRef = FirebaseStorage.getInstance().getReferenceFromUrl(mediaUrl)
            mediaRef.delete().addOnSuccessListener {
                Log.d("ChatFragment", "Associated media deleted successfully.")
            }.addOnFailureListener {
                Log.e("ChatFragment", "Failed to delete associated media.", it)
            }
        }

    }


    private fun uploadFileToFirebase(fileUri: Uri) {
        val fileId = UUID.randomUUID().toString() // Unique ID for the file
        val storageRef = FirebaseStorage.getInstance().reference.child("files/$fileId")
        val uploadTask = storageRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                sendMessageWithFile(downloadUri.toString(), fileUri.lastPathSegment ?: "file")
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }
    private fun sendMessageWithFile(fileUrl: String, fileName: String) {
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
                        fileName,
                        timestamp,
                        "file",
                        fileUrl,
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

        if (requestCode == ChatPersonFragment.REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            }
        }

        if (requestCode == REQUEST_CODE_FILE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadFileToFirebase(uri)
            }
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
        const val REQUEST_CODE_IMAGE_PICK = 1234 // Choose a unique integer.
        const val REQUEST_CODE_FILE_PICK = 1001 // A unique integer value




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