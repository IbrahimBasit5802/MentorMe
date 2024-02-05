package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

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

    }
    }
