package com.ibrahimbasit.I210669

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import kotlin.math.log

class MyFirebaseMessagingService : FirebaseMessagingService() {



    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        super.onNewToken(token)
        // store token in shared preferences
        val pref = applicationContext.getSharedPreferences("MyPref", 0)
        val editor = pref.edit()
        editor.putString("fcmToken", token)
        editor.apply()
        // store token in database
        editor.commit()

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Log.d("FCM", "User ID exists in database.")
                        // User ID exists in Users, set the FCM token here
                        snapshot.ref.child("fcmToken").setValue(token)
                    } else {
                        // User ID does not exist in Users, try setting it in Mentors
                        val mentorRef = FirebaseDatabase.getInstance().getReference("Mentors/$userId")
                        mentorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // Mentor ID exists, set the FCM token here
                                    snapshot.ref.child("fcmToken").setValue(token)
                                } else {
                                    Log.d("FCM", "User/Mentor ID not found in database.")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.w("FCM", "Failed to read value.", error.toException())
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("FCM", "Failed to read value.", error.toException())
                }
            })
        }
        // Save the token in your server or Firebase database
    }


}
