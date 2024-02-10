package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val backButton : View = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val updateButton : Button = findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            finish()
        }
    }
}