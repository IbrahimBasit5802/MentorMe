package com.ibrahimbasit.I210669

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class MentorLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mentor_login)

        val mentorSignUp = findViewById<TextView>(R.id.signUpTextView)
        val mentorLogin = findViewById<Button>(R.id.loginButton)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        mentorSignUp.setOnClickListener {
            val intent = Intent(this, MentorSignUp::class.java)
            startActivity(intent)
        }

        mentorLogin.setOnClickListener {
            loginWithEmail(email.text.toString(), password.text.toString()) { success, message ->
                if (success) {
                    val intent = Intent(this, MentorMainActivity::class.java)
                    startActivity(intent)
                } else {
                    message?.let {
                        // Show error message
                    }
                }
            }
        }



    }

    private fun loginWithEmail(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }
}