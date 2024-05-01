package com.ibrahimbasit.I210669.auth.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.MentorSignUp
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.domain.use_cases.SignUpUseCase
import com.ibrahimbasit.I210669.auth.utils.SignUpViewModelFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: SignUpViewModel
    private  lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDatabase = Firebase.database
        val authRepository = AuthRepository(firebaseAuth, firebaseDatabase, this@SignUpActivity)

        val signUpUseCase = SignUpUseCase(authRepository)
        val viewModelFactory = SignUpViewModelFactory(signUpUseCase)

        // Now use the factory to create the ViewModel
        viewModel = ViewModelProvider(this, viewModelFactory)[SignUpViewModel::class.java]

        val loginLink: TextView = findViewById(R.id.loginLink)
        val phone = findViewById<EditText>(R.id.contactEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val name = findViewById<EditText>(R.id.nameEditText)
        val signupButton: Button = findViewById(R.id.signUpButton)
        val mentorSignUp : TextView = findViewById(R.id.mentorSignUpText)

        mentorSignUp.setOnClickListener {
            val intent = Intent(this, MentorSignUp::class.java)
            startActivity(intent)
        }

        progressBar = findViewById(R.id.signUpProgressBar)

        val countries = getAllCountries()
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, countries)
        val dropdown = findViewById<Spinner>(R.id.countryTextInputLayout)
        dropdown.adapter = adapter
        dropdown.onItemSelectedListener = this

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        signupButton.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            val name = name.text.toString()
            val country = dropdown.selectedItem.toString()
            val city = findViewById<Spinner>(R.id.cityTextInputLayout).selectedItem.toString()
            val contactNumber = phone.text.toString()

            signUpUser(email, password, name, country, city, contactNumber)
        }


        // Observe LiveData from ViewModel for changes and update UI accordingly
        viewModel.userSignUpStatus.observe(this) { status ->
            showLoading(false)

            when (status) {
                is SignUpViewModel.SignUpStatus.VerificationCompleted -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                is SignUpViewModel.SignUpStatus.VerificationFailed -> {
                    Toast.makeText(this, "Error: ${status.error}", Toast.LENGTH_SHORT).show()
                }

                is SignUpViewModel.SignUpStatus.CodeSent -> {
                    Intent(this, VerifyPhoneActivity::class.java).apply {
                        putExtra("token", status.token)
                        putExtra("phone", phone.text.toString())
                        putExtra("email", email.text.toString())
                        putExtra("password", password.text.toString())
                        putExtra("name", name.text.toString())
                        putExtra("country", dropdown.selectedItem.toString())
                        putExtra(
                            "city",
                            findViewById<Spinner>(R.id.cityTextInputLayout).selectedItem.toString()
                        )
                        startActivity(this)
                    }
                }
            }
        }
    }

    private fun signUpUser(email: String, password: String, name: String, country: String, city: String, contactNumber: String) {
        val client = OkHttpClient()
        val jsonObject = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("name", name)
            put("country", country)
            put("city", city)
            put("contactNumber", contactNumber)
        }

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = jsonObject.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("http://192.168.1.8:3000/registerUser")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to register user: ${e.message}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "User registered successfully", Toast.LENGTH_SHORT).show()
                        intent.putExtra("email", email)
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Registration failed: ${response.body?.string()}", Toast.LENGTH_SHORT).show()
                    }
                    showLoading(false)
                }
            }
        })
    }


    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        val cities = getAllCities(parent.getItemAtPosition(position).toString())
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
        val cityDropdown = findViewById<Spinner>(R.id.cityTextInputLayout)
        cityDropdown.adapter = adapter
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Handle the case where no item selection occurs
    }

    // These could be moved to a utility class or the ViewModel
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
