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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ibrahimbasit.I210669.MainActivity
import com.ibrahimbasit.I210669.R
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.domain.use_cases.SignUpUseCase
import com.ibrahimbasit.I210669.auth.utils.SignUpViewModelFactory
import java.util.Locale

class SignUpActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: SignUpViewModel
    private  lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = Firebase.firestore
        val authRepository = AuthRepository(firebaseAuth, firestore)

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
            showLoading(true)

            viewModel.signUpWithPhone(
                phone.text.toString(),
                email.text.toString(),
                password.text.toString(),
                name.text.toString(),
                dropdown.selectedItem.toString(),
                this@SignUpActivity
            )

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
