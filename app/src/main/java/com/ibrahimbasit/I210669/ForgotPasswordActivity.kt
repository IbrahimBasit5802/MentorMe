package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val backButton : View = findViewById(R.id.backArrow);
        backButton.setOnClickListener {
            finish()
        }

        val sendButton : View = findViewById(R.id.sendButton);
        sendButton.setOnClickListener {
            val intent  = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        val loginText : TextView = findViewById(R.id.loginLink)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}