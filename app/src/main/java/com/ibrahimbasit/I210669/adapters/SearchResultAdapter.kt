package com.ibrahimbasit.I210669.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ibrahimbasit.I210669.Mentor
import com.ibrahimbasit.I210669.R

class SearchResultAdapter(private val mentors: List<Mentor>, private val listener: View.OnClickListener) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mentor = mentors[position]
        holder.bind(mentor)
        holder.itemView.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = mentors.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        private val price: TextView = itemView.findViewById(R.id.price)
        private val availableText: TextView = itemView.findViewById(R.id.availability_text)
        private val availableIcon: View = itemView.findViewById(R.id.availabilityIndicator)
        private val favoriteIcon: View = itemView.findViewById(R.id.favoriteIcon)

        fun bind(mentor: Mentor) {
            title.text = mentor.name
            subtitle.text = mentor.role
            price.text = "${mentor.price}/Session"
            availableText.text = if (mentor.availability) "Available" else "Not Available"

            if (mentor.availability) {
                availableIcon.setBackgroundResource(R.drawable.availability_indicator)
                availableText.setTextColor(ContextCompat.getColor(itemView.context, R.color.availableTextColor))
            } else {
                availableIcon.setBackgroundResource(R.drawable.not_available_indicator)
                availableText.setTextColor(ContextCompat.getColor(itemView.context, R.color.unavailableTextColor))
            }

            // Assume all mentors are not favorites for simplicity, update as necessary
            favoriteIcon.setBackgroundResource(R.drawable.not_favorite_icon)
        }
    }
}
