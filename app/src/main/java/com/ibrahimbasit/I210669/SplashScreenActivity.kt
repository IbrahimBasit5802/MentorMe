package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ibrahimbasit.I210669.auth.presentation.LoginActivity
import com.ibrahimbasit.I210669.AgoraEngine
import io.agora.rtc.IRtcEngineEventHandler

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var bar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        bar  = findViewById(R.id.progressBar)


        // intialize agora engine


        val mentor : TextView = findViewById(R.id.mentorTextView)
        mentor.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        };

        val me : TextView = findViewById(R.id.meTextView)
        me.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        };

        AgoraEngine.initialize(this, object : IRtcEngineEventHandler() {
        })

        if (FirebaseAuth.getInstance().currentUser != null) {
            showLoading(true)
            // check if current user is mentor or user by checking reference in realtime database
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        val pref = applicationContext.getSharedPreferences("MyPref", 0)
                        val token = pref.getString("fcmToken", null)

                        showLoading(false)
                        // User ID exists in Users, navigate to user home
                        // updatw user fcm token
                        snapshot.child("fcmToken").ref.setValue(token)
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // User ID does not exist in Users, try setting it in Mentors
                        val mentorRef = FirebaseDatabase.getInstance().getReference("Mentors/$userId")
                        mentorRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    val pref = applicationContext.getSharedPreferences("MyPref", 0)
                                    val token = pref.getString("fcmToken", null)
                                    showLoading(false)
                                    // Mentor ID exists, navigate to mentor home
                                    snapshot.child("fcmToken").ref.setValue(token)

                                    val intent = Intent(this@SplashScreenActivity, MentorMainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    showLoading(false)
                                    Log.d("FCM", "User/Mentor ID not found in database.")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                showLoading(false)
                                Log.w("FCM", "Failed to read value.", error.toException())
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showLoading(false)
                    Log.w("FCM", "Failed to read value.", error.toException())
                }
            })
        }

    }

        private fun showLoading(show: Boolean) {

            // change visibility
            bar.visibility = if (show) {
                CircularProgressIndicator.VISIBLE
            } else {
                CircularProgressIndicator.GONE
            }
        }
    }
