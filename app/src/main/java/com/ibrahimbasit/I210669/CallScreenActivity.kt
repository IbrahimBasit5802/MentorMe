package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CallScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_screen_acitivty)

        val closeButton : View = findViewById(R.id.closeButton)

        closeButton.setOnClickListener {
            finish()
        }
    }
}