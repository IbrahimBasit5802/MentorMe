package com.ibrahimbasit.I210669

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ibrahimbasit.I210669.adapters.MentorChatPersonAdapter
import com.ibrahimbasit.I210669.auth.presentation.LoginActivity

class MentorMainActivity : AppCompatActivity() {
    private lateinit var mentorViewModel: MentorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_main)

        mentorViewModel = ViewModelProvider(this).get(MentorViewModel::class.java)
        FirebaseAuth.getInstance().currentUser?.let { mentorViewModel.loadMentor(it.uid) }

        val chatRecyclerView = findViewById<RecyclerView>(R.id.chatPersonRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        val chatSessionsList = mutableListOf<ChatSession>()

        // Initialize the adapter with an empty list
        // Updated initialization with click listener
        val adapter = MentorChatPersonAdapter(chatSessionsList, object : MentorChatPersonAdapter.OnItemClickListener {
            override fun onItemClick(chatSession: ChatSession) {
                val intent = Intent(this@MentorMainActivity, MentorChatActivity::class.java)
                // Assuming you want to pass some chat session details to the activity
                intent.putExtra("chatSessionId", chatSession.id)
                intent.putExtra("chatSessionName", chatSession.userName)
                startActivity(intent)
            }


        })
        chatRecyclerView.adapter = adapter

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val logout : Button = findViewById(R.id.logoutButton)
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        // Retrieve chat sessions
        currentUserId?.let {
            FirebaseDatabase.getInstance().reference.child("Mentors").child(it)
                .child("chatSessions")
        }?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatSessionIds = snapshot.children.mapNotNull { it.key }

                for (sessionId in chatSessionIds) {
                    val sessionRef =
                        FirebaseDatabase.getInstance().reference.child("Chats").child(sessionId)
                    sessionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(sessionSnapshot: DataSnapshot) {
                            val chatSession = sessionSnapshot.getValue(ChatSession::class.java)

                            chatSession?.let { chatSessionsList.add(it) }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(sessionError: DatabaseError) {
                            Log.d("ChatFragment", "Error fetching chat session details")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors here
            }

        })
    }
}