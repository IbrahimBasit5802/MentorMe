package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BookedSessionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_sessions)

        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}