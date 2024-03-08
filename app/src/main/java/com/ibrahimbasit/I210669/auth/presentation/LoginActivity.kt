package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.ibrahimbasit.I210669.ForgotPasswordActivity
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val signUpLink : TextView = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val forgotPasswordLink : TextView = findViewById(R.id.forgotPasswordTextView);
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        val loginButton : Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}