package com.ibrahimbasit.I210669.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.Mentor

class TopMentorsAdapter(private val mentorList: List<Mentor>) : RecyclerView.Adapter<TopMentorsAdapter.MentorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mentor_card_item, parent, false)
        return MentorViewHolder(view)
    }

    override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
        val mentor = mentorList[position]
        holder.bind(mentor)
    }

    override fun getItemCount(): Int = mentorList.size

    class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.title)
        private val roleTextView: TextView = itemView.findViewById(R.id.subtitle)
        private val availabilityTextView: TextView = itemView.findViewById(R.id.availability_text)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)
        private val availabilityIndicator : View = itemView.findViewById(R.id.availabilityIndicator)

        fun bind(mentor: Mentor) {
            nameTextView.text = mentor.name
            roleTextView.text = mentor.role
            availabilityTextView.text = if (mentor.availability) "Available" else "Not Available"
            if (!mentor.availability) {
                // set color of availability text view
                availabilityTextView.setTextColor(itemView.context.resources.getColor(R.color.unavailableTextColor))
                // set drawable of availability indicator
                availabilityIndicator.background = itemView.context.resources.getDrawable(R.drawable.not_available_indicator)

            }
            priceTextView.text = mentor.price.toString()


        }
    }
}
