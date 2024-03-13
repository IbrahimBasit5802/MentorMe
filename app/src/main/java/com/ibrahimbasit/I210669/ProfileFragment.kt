package com.ibrahimbasit.I210669

import UserViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ibrahimbasit.I210669.auth.models.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userViewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun uploadProfileImage(userId: String, imageUri: Uri, callback: (Boolean, String) -> Unit) {
        val storageRef = Firebase.storage.reference.child("profile_images/$userId.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(true, downloadUri.toString())
            } else {
                callback(false, "Upload failed: ${task.exception?.message}")
            }
        }
    }

    private fun updateUserProfilePicture(userId: String, imageUrl: String) {
        val userRef = Firebase.database.getReference("Users").child(userId)
        userRef.child("profilePictureUrl").setValue(imageUrl).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(activity, "Profile picture updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to update profile picture", Toast.LENGTH_SHORT).show()
            }
        }
    }



    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                userViewModel.userData.value?.uuid?.let { userId ->
                    uploadProfileImage(userId, uri) { success, url ->
                        if (success) {
                            // Update the user's profile picture URL in the Realtime Database
                            updateUserProfilePicture(userId, url)
                        } else {
                            // Handle upload failure
                        }
                    }
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editButton : View = view.findViewById(R.id.edit_profile_button)
        val moreButton : Button = view.findViewById(R.id.more_button)
        val profilePictureImageView: ImageView = view.findViewById(R.id.profilePicture)

        userViewModel.userData.value?.profilePictureUrl?.let { url ->
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.user_profile_picture) // Placeholder image while loading
                .error(R.drawable.user_profile_picture) // Image to display if loading fails
                .into(profilePictureImageView)
        }



        moreButton.setOnClickListener {
            userViewModel.userData.value?.let { userData ->
                val intent = Intent(activity, EditProfileActivity::class.java).apply {
                    putExtra("name", userData.name)
                    putExtra("email", userData.email)
                    putExtra("contact", userData.contactNumber)
                    putExtra("country", userData.country)
                    putExtra("city", userData.city)
                }
                startActivity(intent)
            }
        }

        view.findViewById<TextView>(R.id.nameTextView).text = userViewModel.userData.value?.name ?: "User Name"
        view.findViewById<TextView>(R.id.locationTextView).text = "${userViewModel.userData.value?.city}, ${userViewModel.userData.value?.country}"

        editButton.setOnClickListener {

            // This is typically done using an Intent to open the image chooser

        }


        val editButton2 : View = view.findViewById(R.id.edit_profile_button2)
        editButton2.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
        }

        val bookedSessionsButton : Button = view.findViewById(R.id.bookedSessionsButton)
        bookedSessionsButton.setOnClickListener {
            val intent = Intent(activity, BookedSessionsActivity::class.java)
            startActivity(intent)
        }

        val clickListener = View.OnClickListener {clickedView ->
            val intent = Intent(activity, BookSessionActivity::class.java)
            startActivity(intent)
        }


        userViewModel.userData.observe(viewLifecycleOwner) { user ->
            println("does it work")
            // Update UI with user data
            view.findViewById<TextView>(R.id.nameTextView).text = user.name
            view.findViewById<TextView>(R.id.locationTextView).text = "${user.city}, ${user.country}"
            user.profilePictureUrl.let { url ->
                Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.user_profile_picture) // Placeholder image while loading
                    .error(R.drawable.user_profile_picture) // Image to display if loading fails
                    .into(profilePictureImageView)
            }
            // Rest of your UI update code...
        }


        view.findViewById<View>(R.id.mentorbox).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.mentorbox2).setOnClickListener(clickListener)
        view.findViewById<View>(R.id.mentorbox3).setOnClickListener(clickListener)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val REQUEST_CODE_IMAGE_PICK = 1234 // Choose a unique integer.

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}