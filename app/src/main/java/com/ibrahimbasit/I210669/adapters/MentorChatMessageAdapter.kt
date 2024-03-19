import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.ChatMessage
import com.ibrahimbasit.I210669.R
import com.squareup.picasso.Picasso
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MentorChatMessageAdapter(private val userId: String, private val messages: List<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val VIEW_TYPE_AUDIO_SENT = 3
    private val VIEW_TYPE_AUDIO_RECEIVED = 4


    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return when {
            message.senderId == userId && message.type == "voice" -> VIEW_TYPE_AUDIO_SENT
            message.senderId != userId && message.type == "voice" -> VIEW_TYPE_AUDIO_RECEIVED
            message.senderId == userId -> VIEW_TYPE_MESSAGE_SENT
            else -> VIEW_TYPE_MESSAGE_RECEIVED
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.sender_message_item, parent, false)
            VIEW_TYPE_MESSAGE_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.receiver_message_item, parent, false)
            VIEW_TYPE_AUDIO_SENT -> LayoutInflater.from(parent.context).inflate(R.layout.voice_note_sent, parent, false)
            VIEW_TYPE_AUDIO_RECEIVED -> LayoutInflater.from(parent.context).inflate(R.layout.voice_note_received, parent, false)
            else -> throw IllegalArgumentException("Invalid view type")
        }
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> SentMessageHolder(view)
            VIEW_TYPE_MESSAGE_RECEIVED -> ReceivedMessageHolder(view)
            VIEW_TYPE_AUDIO_SENT -> SentAudioMessageHolder(view)
            VIEW_TYPE_AUDIO_RECEIVED -> ReceivedAudioMessageHolder(view)
            else -> throw IllegalArgumentException("Invalid view type for creating view holder")
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

            val dbRef = Firebase.database.getReference("Users").child(message.senderId)
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
            val dbRef = Firebase.database.getReference("Users").child(message.senderId)
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
