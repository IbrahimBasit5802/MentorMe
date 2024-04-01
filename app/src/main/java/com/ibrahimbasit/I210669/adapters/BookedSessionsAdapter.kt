package com.ibrahimbasit.i210669.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.Booking

import com.squareup.picasso.Picasso

class BookedSessionsAdapter(private val context: Context, private val bookingsList: List<Booking>) : RecyclerView.Adapter<BookedSessionsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mentorImage: ImageView = view.findViewById(R.id.mentorImage2)
        val name: TextView = view.findViewById(R.id.name)
        val subtitle: TextView = view.findViewById(R.id.subtitle)
        val date: TextView = view.findViewById(R.id.date)
        val time: TextView = view.findViewById(R.id.time)
        // Consider adding other views here if you plan to display them, like duration, status, etc.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booked_session_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookingsList[position]


        Firebase.database.reference.child("Mentors").child(booking.mentorId).get().addOnSuccessListener { dataSnapshot ->
            val profilePictureUrl = dataSnapshot.child("profilePictureUrl").value.toString()
            val name = dataSnapshot.child("name").value.toString()
            val subtitle = dataSnapshot.child("role").value.toString()
            holder.name.text = name // Implement this function based on your data.
            holder.subtitle.text = subtitle // Implement this function based on your data.
            Picasso.get().load(profilePictureUrl).into(holder.mentorImage)



        }


        holder.date.text = booking.date
        holder.time.text = booking.time
        // Here you can set other fields like duration and status as needed.
    }

    override fun getItemCount(): Int {
        return bookingsList.size
    }


}

