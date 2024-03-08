package com.ibrahimbasit.I210669.auth.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class FirebaseAuthService {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    firebaseAuth.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onSuccess()
                        } else {
                            onFailure(updateTask.exception ?: Exception("Failed to update user profile"))
                        }
                    }
                } else {
                    onFailure(task.exception ?: Exception("Sign up failed"))
                }
            }
    }
}
