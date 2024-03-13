package com.ibrahimbasit.I210669

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ibrahimbasit.I210669.auth.models.User
import java.util.Locale

class EditProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var countrySpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var nameText: EditText
    private lateinit var emailText: EditText
    private lateinit var contactText: EditText
    private var selectedCountry: String? = null
    private var selectedCity: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
         nameText = findViewById(R.id.name_edit_text)
        emailText = findViewById(R.id.email_edit_text)
        contactText= findViewById(R.id.contact_edit_text)
        countrySpinner = findViewById(R.id.countryTextInputLayout)
        citySpinner = findViewById(R.id.cityTextInputLayout)

        selectedCountry = intent.getStringExtra("country")
        selectedCity = intent.getStringExtra("city")

        val countries = getAllCountries()
        val countryAdapter = ArrayAdapter(this, R.layout.dropdown_item, countries)
        countrySpinner.adapter = countryAdapter
        countrySpinner.onItemSelectedListener = this

        selectedCountry?.let {
            val countryPosition = countryAdapter.getPosition(it)
            if (countryPosition >= 0) {
                countrySpinner.setSelection(countryPosition)
            }
        }

        nameText.setText(intent.getStringExtra("name") ?: "")
        emailText.setText(intent.getStringExtra("email") ?: "")
        contactText.setText(intent.getStringExtra("contact") ?: "")

        val backButton : View = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val updateButton : Button = findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            updateUserData()

            finish()
        }
    }

    private fun updateUserData() {
        val newName = nameText.text.toString().trim()
        val newEmail = emailText.text.toString().trim()
        val newContact = contactText.text.toString().trim()
        val newCountry = countrySpinner.selectedItem.toString()
        val newCity = citySpinner.selectedItem.toString()



        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (user != null) {
            if (newEmail != user.email) {
                var updatedEmail = false
                FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail)
                    ?.addOnSuccessListener {
                        updatedEmail = true
                        Log.d("EditProfileActivity", "User email updated successfully")
                    }
                    ?.addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to update user email: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                if (!updatedEmail) {
                    return
                }
            }
        }

        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(it)
            val updatedUserData = hashMapOf(
                "name" to newName,
                "email" to newEmail,
                "contactNumber" to newContact,
                "country" to newCountry,
                "city" to newCity,
                "uuid" to it,
                "profilePictureUrl" to "gs://i210669.appspot.com/user_profile_picture.png"
            )

            println("fucking hell")
            databaseReference.updateChildren(updatedUserData as Map<String, Any>)
                .addOnSuccessListener {
                    println("fucking success")
                    Toast.makeText(this, "User data updated successfully", Toast.LENGTH_SHORT).show()
                    Log.d("EditProfileActivity", "User data updated successfully")
                    finish()
                }
                .addOnFailureListener { exception ->
                    println("fucking failure")

                    Toast.makeText(this, "Failed to update user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        if (parent.id == R.id.countryTextInputLayout) {
            val selectedCountry = parent.getItemAtPosition(position).toString()
            val cities = getAllCities(selectedCountry)
            val cityAdapter = ArrayAdapter(this, R.layout.dropdown_item, cities)
            citySpinner.adapter = cityAdapter

            // Here, after setting the city adapter based on the country, set the city selection
            // This needs to be delayed to ensure the adapter is set first
            selectedCity?.let {
                val cityPosition = cityAdapter.getPosition(it)
                if (cityPosition >= 0) {
                    citySpinner.post { citySpinner.setSelection(cityPosition) }
                }
            }
        }
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