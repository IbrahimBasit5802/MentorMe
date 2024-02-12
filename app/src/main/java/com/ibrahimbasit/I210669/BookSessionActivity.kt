package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class BookSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)

        val booksessionButton : Button = findViewById(R.id.bookButton)

        booksessionButton.setOnClickListener {
            val intent = Intent(this, SelectSessionActivity::class.java)
            startActivity(intent)
        }

        val backButton : Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
    }
}