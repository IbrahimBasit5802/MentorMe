package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val submitFeedback : Button = findViewById(R.id.feedbackButton)
        submitFeedback.setOnClickListener {
            finish()
        }
    }
}