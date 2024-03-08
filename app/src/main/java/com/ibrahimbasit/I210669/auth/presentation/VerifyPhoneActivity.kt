package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.auth.models.User

class VerifyPhoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        val backButton : View = findViewById(R.id.backArrow);
        backButton.setOnClickListener {
            finish()
        }

        val verifyButton : View = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val editTexts = listOf(
            findViewById<EditText>(R.id.pin1EditText),
            findViewById<EditText>(R.id.pin2EditText),
            findViewById<EditText>(R.id.pin3EditText),
            findViewById<EditText>(R.id.pin4EditText),
            findViewById<EditText>(R.id.pin5EditText),
            findViewById<EditText>(R.id.pin6EditText)
        )





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
        var otp = getIntent().getStringExtra("token")




        verifyButton.setOnClickListener {
            val pin = editTexts.joinToString("") { it.text.toString() }
            var credential = PhoneAuthProvider.getCredential(otp!!, pin)

            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        getIntent().getStringExtra("email")!!,
                        getIntent().getStringExtra("password")!!).addOnSuccessListener {
                            // get user uuid, create User model, and save it to the database
                            var user = User(
                                getIntent().getStringExtra("name")!!,
                                FirebaseAuth.getInstance().currentUser?.uid!!,
                                getIntent().getStringExtra("email")!!,
                                getIntent().getStringExtra("phone")!!,
                                getIntent().getStringExtra("country")!!,
                                getIntent().getStringExtra("city")!!,

                            )

                        val db = Firebase.firestore
                        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.uid!!).set(user)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent)
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                            }

                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
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

    private fun deleteButtonClicked(editTexts: List<EditText>) {
        editTexts.findLast { it.text.isNotEmpty() }?.apply {
            setText("")
            requestFocus()
        }

    }
}