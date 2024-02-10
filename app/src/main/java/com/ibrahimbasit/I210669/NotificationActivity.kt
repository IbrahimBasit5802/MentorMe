package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

    }
}