package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.domain.use_cases.VerifyPhoneUseCase
import com.ibrahimbasit.I210669.auth.models.User
import com.ibrahimbasit.I210669.auth.utils.VerifyPhoneViewModelFactory

class VerifyPhoneActivity : AppCompatActivity() {
    private lateinit var viewModel: VerifyPhoneViewModel
    private  lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)
        val editTexts = listOf(
            findViewById<EditText>(R.id.pin1EditText),
            findViewById<EditText>(R.id.pin2EditText),
            findViewById<EditText>(R.id.pin3EditText),
            findViewById<EditText>(R.id.pin4EditText),
            findViewById<EditText>(R.id.pin5EditText),
            findViewById<EditText>(R.id.pin6EditText)
        )

        progressBar = findViewById(R.id.verifyProgressBar)

        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count == 1 && index < editTexts.size - 1) {
                        editTexts[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editText.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        editTexts[index - 1].requestFocus()
                    }
                }
                false
            })
        }
        val numberButtons = listOf(
            findViewById<TextView>(R.id.number0),
            findViewById<TextView>(R.id.number1),
            findViewById<TextView>(R.id.number2),
            findViewById<TextView>(R.id.number3),
            findViewById<TextView>(R.id.number4),
            findViewById<TextView>(R.id.number5),
            findViewById<TextView>(R.id.number6),
            findViewById<TextView>(R.id.number7),
            findViewById<TextView>(R.id.number8),
            findViewById<TextView>(R.id.number9)
        )

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = Firebase.database
        val authRepo = AuthRepository(firebaseAuth, firebaseDatabase)

        val verifyUseCase = VerifyPhoneUseCase(authRepo, intent.getStringExtra("name").toString(), intent.getStringExtra("phone").toString(), intent.getStringExtra("country").toString(), intent.getStringExtra("city").toString())

        val viewModelFactory = VerifyPhoneViewModelFactory(verifyUseCase)
        viewModel = ViewModelProvider(this, viewModelFactory)[VerifyPhoneViewModel::class.java]

        val backButton : View = findViewById(R.id.backArrow);
        backButton.setOnClickListener {
            finish()
        }

        val verifyButton: View = findViewById(R.id.verifyButton)
        verifyButton.setOnClickListener {
            showLoading(true)
            val pin = editTexts.joinToString("") { it.text.toString() }
            val otp = intent.getStringExtra("token") ?: return@setOnClickListener
            val credential = PhoneAuthProvider.getCredential(otp, pin)
            val email = intent.getStringExtra("email") ?: ""
            val password = intent.getStringExtra("password") ?: ""
            viewModel.verifyPhoneWithCode(credential, email, password)
        }

        viewModel.verificationState.observe(this) { state ->
            showLoading(false)
            when (state) {
                is VerifyPhoneViewModel.VerificationState.Success -> {
                    showLoading(false)
                    navigateToMainActivity()
                }
                is VerifyPhoneViewModel.VerificationState.Failure -> {
                    showLoading(false)
                    showError(state.message)
                }
            }
        }


        numberButtons.forEach { textView ->
            textView.setOnClickListener { numberButtonClicked(textView.text.toString(), editTexts) }
        }

        // Set click listener for the delete button
        findViewById<View>(R.id.delete).setOnClickListener { deleteButtonClicked(editTexts) }
    }

    private fun numberButtonClicked(number: String, editTexts: List<EditText>) {
        editTexts.firstOrNull { it.text.isEmpty() }?.apply {
            setText(number)
            if (editTexts.last() != this) {
                editTexts[editTexts.indexOf(this) + 1].requestFocus()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        // Optionally handle user deletion here if necessary
    }

    private fun deleteButtonClicked(editTexts: List<EditText>) {
        editTexts.findLast { it.text.isNotEmpty() }?.apply {
            setText("")
            requestFocus()
        }

    }
}