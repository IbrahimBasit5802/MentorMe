package com.ibrahimbasit.I210669.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimbasit.I210669.ChatSession
import com.ibrahimbasit.I210669.R
import com.squareup.picasso.Picasso

class MentorChatPersonAdapter(private val chatList: List<ChatSession>, private val listener: OnItemClickListener) : RecyclerView.Adapter<MentorChatPersonAdapter.MentorChatPersonViewHolder>() {

    inner class MentorChatPersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        val nameTextView: TextView = itemView.findViewById(R.id.name_text)
        val messageStatusTextView: TextView = itemView.findViewById(R.id.message_status_text)

    }

    interface OnItemClickListener {
        fun onItemClick(chatSession: ChatSession)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorChatPersonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.chat_person_item, parent, false)

        return MentorChatPersonViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MentorChatPersonViewHolder, position: Int) {
        val currentItem = chatList[position]
        // Set image using Glide or similar library
        holder.nameTextView.text = currentItem.userName
        val newMessagesText = when {
            currentItem.newMessagesCount > 0 -> "${currentItem.newMessagesCount} New Message(s)"
            else -> "No New Messages"
        }

        holder.messageStatusTextView.text = newMessagesText

        Picasso.get().load(currentItem.userProfilePictureUrl).fit().into(holder.profileImageView)
        holder.itemView.setOnClickListener {
            listener.onItemClick(currentItem)
        }


    }

    override fun getItemCount() = chatList.size
}