package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R
import java.util.Locale
import java.util.concurrent.TimeUnit


class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val loginLink : TextView = findViewById(R.id.loginLink)
        var auth = FirebaseAuth.getInstance()

        val phone = findViewById<EditText>(R.id.contactEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val name = findViewById<EditText>(R.id.nameEditText)




        loginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val signupButton : Button = findViewById(R.id.signUpButton)


        val countries = getAllCountries() // Get your list of countries
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, countries)
        val dropdown = findViewById<Spinner>(R.id.countryTextInputLayout)
        dropdown.setAdapter(adapter)

        dropdown.onItemSelectedListener = this

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                auth.signInWithCredential(p0).addOnSuccessListener {
                    val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@SignUpActivity, "Error: ${p0.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                val intent = Intent(this@SignUpActivity, VerifyPhoneActivity::class.java)
                intent.putExtra("token", p0)
                intent.putExtra("phone", phone.text.toString())
                intent.putExtra("email", email.text.toString())
                intent.putExtra("password", password.text.toString())
                intent.putExtra("name", name.text.toString())
                intent.putExtra("country", dropdown.selectedItem.toString())
                intent.putExtra("city", findViewById<Spinner>(R.id.cityTextInputLayout).selectedItem.toString())
                startActivity(intent)
            }
        }

        signupButton.setOnClickListener {
            val options = PhoneAuthOptions.newBuilder().setPhoneNumber(phone.text.toString()).setTimeout(60L, TimeUnit.SECONDS).setActivity(this)
                .setCallbacks(callbacks).build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val cities = getAllCities(parent?.getItemAtPosition(position).toString())
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        val dropdown = findViewById<Spinner>(R.id.cityTextInputLayout)
        dropdown.setAdapter(adapter)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }


    private fun getAllCountries(): Array<String> {
        return Locale.getAvailableLocales().map { Locale("", it.country).displayCountry }.distinct().sorted().toTypedArray()
    }

    private fun getAllCities(country: String): Array<String> {

        val citiesMap = mapOf(
            "USA" to arrayOf("New York", "Los Angeles", "Chicago"),
            "Canada" to arrayOf("Toronto", "Vancouver", "Montreal")
        )

        return citiesMap[country] ?: arrayOf("No cities available for this country")
    }
}