package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.ForgotPasswordActivity
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.domain.use_cases.LoginUseCase
import com.ibrahimbasit.I210669.auth.utils.LoginActivityViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: LoginViewModel
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val signUpLink : TextView = findViewById(R.id.signUpLink);
        progressBar = findViewById(R.id.loginProgressBar)
        val firebaseAuth = FirebaseAuth.getInstance()
        val realtimeDatabase = Firebase.database

        val repo = AuthRepository(firebaseAuth, realtimeDatabase, this@LoginActivity)
        val loginUsecase = LoginUseCase(repo)
        val viewModelFactory = LoginActivityViewModelFactory(loginUsecase)
        viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

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
            showLoading(true)
            val email = findViewById<TextView>(R.id.emailEditText).text.toString()
            val password = findViewById<TextView>(R.id.passwordEditText).text.toString()
            viewModel.login(email, password)
        }

        viewModel.userLoginStatus.observe(this) { loginStatus ->
            showLoading(false)
            when (loginStatus) {
                is LoginViewModel.LoginStatus.LoginSuccessful -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }

                is LoginViewModel.LoginStatus.LoginFailed -> {
                    Toast.makeText(this, loginStatus.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}