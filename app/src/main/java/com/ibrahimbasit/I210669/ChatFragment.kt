package com.ibrahimbasit.I210669

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val chatRecyclerView = view.findViewById<RecyclerView>(R.id.chatPersonRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
        val chatSessionsList = mutableListOf<ChatSession>()

        // Initialize the adapter with an empty list
        val adapter = ChatPersonAdapter(chatSessionsList, object : ChatPersonAdapter.OnItemClickListener {
            override fun onItemClick(chatSession: ChatSession) {
                val fragment = ChatPersonFragment.newInstance(chatSession.id, chatSession.mentorName)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, fragment)
                    ?.addToBackStack(null) // If you want to add it to back stack
                    ?.commit()
            }
        })

        chatRecyclerView.adapter = adapter

        // Get current user ID
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        // Retrieve chat sessions
        val chatSessionsRef =
            currentUserId?.let { FirebaseDatabase.getInstance().reference.child("Users").child(it).child("chatSessions") }
        if (chatSessionsRef != null) {
            chatSessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatSessionIds = snapshot.children.map { it.key }.filterNotNull()
                    // Now fetch the details for each chat session ID

                    for (sessionId in chatSessionIds) {
                        val sessionRef = FirebaseDatabase.getInstance().reference.child("Chats").child(sessionId)
                        sessionRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(sessionSnapshot: DataSnapshot) {
                                val chatSession = sessionSnapshot.getValue(ChatSession::class.java)

                                chatSession?.let { chatSessionsList.add(it) }
                                // Once all sessions are added, update the adapter
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

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}