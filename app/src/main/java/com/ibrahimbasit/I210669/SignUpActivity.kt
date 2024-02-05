package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val loginLink : TextView = findViewById(R.id.loginLink)

        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val signupButton : Button = findViewById(R.id.signUpButton)
        signupButton.setOnClickListener {
            val intent = Intent(this, VerifyPhoneActivity::class.java)
            startActivity(intent)
        }
    }
}