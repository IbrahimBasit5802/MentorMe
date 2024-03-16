package com.ibrahimbasit.I210669

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MentorViewModel : ViewModel() {
    private val _mentor = MutableLiveData<Mentor>()
    val mentor: LiveData<Mentor> = _mentor

    fun loadMentor(mentorId: String) {
        val databaseRef = Firebase.database.getReference("Mentors").child(mentorId)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mentor = snapshot.getValue(Mentor::class.java)
                _mentor.value = mentor
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
