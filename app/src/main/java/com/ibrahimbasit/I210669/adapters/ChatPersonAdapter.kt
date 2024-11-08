package com.ibrahimbasit.I210669

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ChatPersonAdapter(private val chatList: List<ChatSession>, private val listener: OnItemClickListener) : RecyclerView.Adapter<ChatPersonAdapter.ChatPersonViewHolder>() {

    inner class ChatPersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_text)
        val messageStatusTextView: TextView = itemView.findViewById(R.id.message_status_text)

    }

    interface OnItemClickListener {
        fun onItemClick(chatSession: ChatSession)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPersonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_person_item, parent, false)
        val viewHolder = ChatPersonViewHolder(itemView)
        itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(chatList[position])
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatPersonViewHolder, position: Int) {
        val currentItem = chatList[position]
        // Set image using Glide or similar library
        holder.nameTextView.text = currentItem.mentorName
        val newMessagesText = when {
            currentItem.newMessagesCount > 0 -> "${currentItem.newMessagesCount} New Message(s)"
            else -> "No New Messages"
        }

        holder.messageStatusTextView.text = newMessagesText

        Picasso.get().load(currentItem.mentorProfilePictureUrl).fit().into(holder.profileImageView)


    }

    override fun getItemCount() = chatList.size
}
