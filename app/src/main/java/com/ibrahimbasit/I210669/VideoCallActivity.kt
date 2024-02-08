package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class VideoCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        val closeButton : View = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }
    }
}