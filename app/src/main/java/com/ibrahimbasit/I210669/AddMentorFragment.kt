package com.ibrahimbasit.I210669

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddMentorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddMentorFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var databaseReference: DatabaseReference
    private var imageUri: Uri? = null // Holds the selected image URI
    private  lateinit var progressBar: ProgressBar



    // UI Components
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var roleEditText: EditText
    private lateinit var statusSpinner: Spinner
    private lateinit var uploadButton: Button

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AddMentorFragment.REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            val text = view?.findViewById<TextView>(R.id.uploadPicText)
            text?.text = "Image selected"

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_mentor, container, false)
        databaseReference = FirebaseDatabase.getInstance().getReference("Mentors")

        // Bind UI components
        nameEditText = view.findViewById(R.id.nameEditText)
        descriptionEditText = view.findViewById(R.id.descriptionEditText)
        priceEditText = view.findViewById(R.id.priceEditText)
        roleEditText = view.findViewById(R.id.roleEditText)
        statusSpinner = view.findViewById(R.id.statusTextInputLayout)
        uploadButton = view.findViewById(R.id.uploadButton)
        progressBar = view.findViewById(R.id.addMentorProgressBar)
        val uploadPicContainer : View = view.findViewById(R.id.picUploadContainer)

        uploadPicContainer.setOnClickListener {
            pickImageFromGallery()
        }

        val countries = arrayOf("Available", "Not Available")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, countries)

        statusSpinner = view.findViewById(R.id.statusTextInputLayout)
        statusSpinner.adapter = adapter
        statusSpinner.setSelection(0)

        uploadButton.setOnClickListener {
            if (imageUri != null) {
                showLoading(true)

                uploadMentor()
            }


        }
        return view
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun uploadMentor() {
        // Assuming you have implemented methods to get these values from your UI
        val name = nameEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val price = priceEditText.text.toString().toFloatOrNull() ?: 0f
        val role = roleEditText.text.toString()
        val availability = statusSpinner.selectedItem.toString() == "Available"

        // Create a new Mentor object
        val mentor = Mentor("", name, role, availability, "", price, description, 0f)
        val mentorId = databaseReference.push().key ?: return

        mentor.mentorId = mentorId

        // First, add the mentor to the database
        databaseReference.child(mentorId).setValue(mentor).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // If mentor data is successfully added, upload the image next
                imageUri?.let { uri ->
                    uploadImageToFirebaseStorage(mentorId, uri)
                }
            } else {
                showLoading(false)
                Toast.makeText(context, "Failed to add mentor. Please try again", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun uploadImageToFirebaseStorage(mentorId: String, imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().getReference("mentor_profile_images/$mentorId.jpg")
        storageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                // After image upload, update the mentor's profilePictureUrl
                updateMentorProfilePictureUrl(mentorId, downloadUri.toString())
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(context, "Image upload failed: ${it.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateMentorProfilePictureUrl(mentorId: String, profilePictureUrl: String) {
        databaseReference.child(mentorId).child("profilePictureUrl").setValue(profilePictureUrl).addOnSuccessListener {
            showLoading(false)
            Toast.makeText(context, "Mentor profile updated with image.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(context, "Failed to update mentor's profile picture.", Toast.LENGTH_SHORT).show()
        }
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddMentorFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val REQUEST_CODE_IMAGE_PICK = 1234 // Choose a unique integer.

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddMentorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, AddMentorFragment.REQUEST_CODE_IMAGE_PICK)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}