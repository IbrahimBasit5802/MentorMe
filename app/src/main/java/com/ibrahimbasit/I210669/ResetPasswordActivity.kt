package com.ibrahimbasit.I210669

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ibrahimbasit.I210669.auth.presentation.LoginActivity

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        val backButton : View = findViewById(R.id.backArrow)
        backButton.setOnClickListener {
            finish()
        }
        val resetButton : Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val loginText : TextView = findViewById(R.id.loginLink)
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}