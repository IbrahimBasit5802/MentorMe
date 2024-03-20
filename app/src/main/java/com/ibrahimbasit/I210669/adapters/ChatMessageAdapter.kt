import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.ChatMessage
import com.ibrahimbasit.I210669.R
import com.squareup.picasso.Picasso
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class ChatMessageAdapter(private val userId: String, private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val VIEW_TYPE_AUDIO_SENT = 3
    private val VIEW_TYPE_AUDIO_RECEIVED = 4
    private val VIEW_TYPE_IMAGE_SENT = 5
    private val VIEW_TYPE_IMAGE_RECEIVED = 6
    private val VIEW_TYPE_FILE_SENT = 7
    private val VIEW_TYPE_FILE_RECEIVED = 8



    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.senderId == userId && message.type == "voice" -> VIEW_TYPE_AUDIO_SENT
            message.senderId != userId && message.type == "voice" -> VIEW_TYPE_AUDIO_RECEIVED
            message.senderId == userId && message.type == "image" -> VIEW_TYPE_IMAGE_SENT
            message.senderId != userId && message.type == "image" -> VIEW_TYPE_IMAGE_RECEIVED
            message.senderId == userId && message.type == "file" -> VIEW_TYPE_FILE_SENT
            message.senderId != userId && message.type == "file" -> VIEW_TYPE_FILE_RECEIVED
            message.senderId == userId -> VIEW_TYPE_MESSAGE_SENT
            else -> VIEW_TYPE_MESSAGE_RECEIVED
        }
    }



    fun getItemAtPosition(position: Int): ChatMessage {
        return messages[position]
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.sender_message_item, parent, false)
            VIEW_TYPE_MESSAGE_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.receiver_message_item, parent, false)
            VIEW_TYPE_AUDIO_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.voice_note_sent, parent, false)
            VIEW_TYPE_AUDIO_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.voice_note_received, parent, false)
            VIEW_TYPE_IMAGE_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.image_sent_item, parent, false)
            VIEW_TYPE_IMAGE_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.image_received_item, parent, false)
            VIEW_TYPE_FILE_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.file_sent_item, parent, false)
            VIEW_TYPE_FILE_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.file_received_item, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> SentMessageHolder(view)
            VIEW_TYPE_MESSAGE_RECEIVED -> ReceivedMessageHolder(view)
            VIEW_TYPE_AUDIO_SENT -> SentAudioMessageHolder(view)
            VIEW_TYPE_AUDIO_RECEIVED -> ReceivedAudioMessageHolder(view)
            VIEW_TYPE_IMAGE_SENT -> ImageMessageSentHolder(view)
            VIEW_TYPE_IMAGE_RECEIVED -> ImageMessageReceivedHolder(view)
            VIEW_TYPE_FILE_SENT -> FileMessageSentHolder(view)
            VIEW_TYPE_FILE_RECEIVED -> FileMessageReceivedHolder(view)
            else -> throw IllegalArgumentException("Invalid view type for creating view holder")
        }
    }

    fun playAudio(audioUrl: String) {
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setOnPreparedListener { start() }
            setOnErrorListener { mp, what, extra ->
                Log.e("ChatMessageAdapter", "Error playing audio: $what, $extra")
                mp.release()
                false
            }
            setOnCompletionListener {
                it.release()
            }
            try {
                setDataSource(audioUrl)
                prepareAsync() // Asynchronous preparation
            } catch (e: IOException) {
                Log.e("ChatMessageAdapter", "Could not prepare media player", e)
            }
        }
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> (holder as SentMessageHolder).bind(message)
            VIEW_TYPE_MESSAGE_RECEIVED -> (holder as ReceivedMessageHolder).bind(message)
            VIEW_TYPE_AUDIO_SENT -> (holder as SentAudioMessageHolder).bind(message)
            VIEW_TYPE_AUDIO_RECEIVED -> (holder as ReceivedAudioMessageHolder).bind(message)
            VIEW_TYPE_IMAGE_SENT -> (holder as ImageMessageSentHolder).bind(message)
            VIEW_TYPE_IMAGE_RECEIVED -> (holder as ImageMessageReceivedHolder).bind(message)
            VIEW_TYPE_FILE_SENT -> (holder as FileMessageSentHolder).bind(message)
            VIEW_TYPE_FILE_RECEIVED -> (holder as FileMessageReceivedHolder).bind(message)
        }
    }

    fun downloadAndOpenFile(context: Context, fileUrl: String?, fileName: String?) {
        if (fileUrl == null || fileName == null) return

        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val downloadedFileUri = downloadManager.getUriForDownloadedFile(downloadId)

                if (downloadedFileUri != null) {
                    val openFileIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(downloadedFileUri, context?.contentResolver?.getType(downloadedFileUri))
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context?.startActivity(openFileIntent)
                }
            }
        }

        context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private inner class FileMessageSentHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.fileNameSent)
        private val messageTime: TextView = view.findViewById(R.id.fileMessageTimeSent)

        fun bind(message: ChatMessage) {
            fileName.text = message.message // Assuming you have a fileName field in your ChatMessage model.
            messageTime.text = formatTimestamp(message.timestamp)
        }
    }

    private inner class FileMessageReceivedHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val fileName: TextView = view.findViewById(R.id.fileNameReceived)
        private val messageTime: TextView = view.findViewById(R.id.fileMessageTimeReceived)
        private val profileImage: ImageView = view.findViewById(R.id.receiveDp)

        init {
            view.setOnClickListener {
                val message = messages[adapterPosition]
                downloadAndOpenFile(view.context, message.mediaUrl, message.message)
            }
        }

        fun bind(message: ChatMessage) {
            fileName.text = message.message // Assuming you have a fileName field in your ChatMessage model.
            messageTime.text = formatTimestamp(message.timestamp)
            // Load the sender's profile picture
            Firebase.database.reference.child("Mentors").child(message.senderId).get().addOnSuccessListener { dataSnapshot ->
                val profilePictureUrl = dataSnapshot.child("profilePictureUrl").value.toString()
                Picasso.get().load(profilePictureUrl).into(profileImage)
            }        }
    }


    private inner class ImageMessageSentHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMessage: ImageView = view.findViewById(R.id.imgMessage)
        val timestampText: TextView = view.findViewById(R.id.messageTime)

        fun bind(message: ChatMessage) {
            Picasso.get().load(message.mediaUrl).into(imgMessage)
            timestampText.text = formatTimestamp(message.timestamp)
        }
    }

    // View holder for received image messages
    private inner class ImageMessageReceivedHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMessage: ImageView = view.findViewById(R.id.imgMessage)
        val timestampText: TextView = view.findViewById(R.id.messageTime)
        val imgProfile: ImageView = view.findViewById(R.id.receiveDp) // Assuming you have a profile picture in your layout

        fun bind(message: ChatMessage) {
            Picasso.get().load(message.mediaUrl).into(imgMessage)
            timestampText.text = formatTimestamp(message.timestamp)
            // Load profile picture if available
            Firebase.database.reference.child("Mentors").child(message.senderId).get().addOnSuccessListener { dataSnapshot ->
                val profilePictureUrl = dataSnapshot.child("profilePictureUrl").value.toString()
                Picasso.get().load(profilePictureUrl).into(imgProfile)
            }
        }
    }

    private inner class SentAudioMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: ChatMessage) {
            val playButton = itemView.findViewById<ImageView>(R.id.playAudioButton)
            val durationText = itemView.findViewById<TextView>(R.id.audioDuration)
            val timestampText = itemView.findViewById<TextView>(R.id.messageTime)
            durationText.text = "0:30"

            timestampText.text = formatTimestamp(message.timestamp)// Format timestamp to readable time

            playButton.setOnClickListener {
                // Implement playback functionality
                playAudio(message.mediaUrl ?: "")
            }
        }
    }

    private inner class ReceivedAudioMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: ChatMessage) {
            val playButton = itemView.findViewById<ImageView>(R.id.playAudioButton)
            val durationText = itemView.findViewById<TextView>(R.id.audioDuration)
            val timestampText = itemView.findViewById<TextView>(R.id.messageTime)

            val dbRef = Firebase.database.getReference("Mentors").child(message.senderId)
            dbRef.get().addOnSuccessListener {
                val dp = it.child("profilePictureUrl").value.toString()
                Picasso.get().load(dp).fit().into(itemView.findViewById<ImageView>(R.id.receiveDp))
            }.addOnFailureListener {
            }

            // Placeholder for actual duration
            durationText.text = "0:30"

            timestampText.text = formatTimestamp(message.timestamp)// Format timestamp to readable time

            playButton.setOnClickListener {
                // Implement playback functionality
                playAudio(message.mediaUrl ?: "")
            }
        }
    }


    private inner class SentMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: ChatMessage) {
            itemView.findViewById<TextView>(R.id.messageText).text = message.message
            itemView.findViewById<TextView>(R.id.messageTime).text = formatTimestamp(message.timestamp)// Format timestamp to readable time
        }
    }

    private inner class ReceivedMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: ChatMessage) {
            itemView.findViewById<TextView>(R.id.messageText).text = message.message
            itemView.findViewById<TextView>(R.id.messageTime).text = formatTimestamp(message.timestamp)// Format timestamp to readable time
            val dbRef = Firebase.database.getReference("Mentors").child(message.senderId)
            dbRef.get().addOnSuccessListener {
                val dp = it.child("profilePictureUrl").value.toString()
                Picasso.get().load(dp).fit().into(itemView.findViewById<ImageView>(R.id.receiveDp))
            }.addOnFailureListener {
            }
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
