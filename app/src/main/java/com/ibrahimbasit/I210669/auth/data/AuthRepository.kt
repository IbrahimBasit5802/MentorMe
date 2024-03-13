package com.ibrahimbasit.I210669.auth.data

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.ibrahimbasit.I210669.auth.models.User
import java.util.concurrent.TimeUnit

class AuthRepository(private val firebaseAuth: FirebaseAuth, private val firebaseDatabase: FirebaseDatabase) {

    interface Callback {
        fun onVerificationCompleted()
        fun onVerificationFailed(error: String)
        fun onCodeSent(token: String, resendingToken: PhoneAuthProvider.ForceResendingToken)

    }

    fun signUpWithPhone(phone: String, email: String, password: String, name: String, country: String, activity: AppCompatActivity, callback: Callback) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Sign in user with the credential
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // This can be omitted if you handle user sign-in elsewhere
                        callback.onVerificationCompleted()
                    } else {
                        task.exception?.message?.let {
                            callback.onVerificationFailed(it)
                        }
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                callback.onVerificationFailed(e.localizedMessage ?: "Verification failed, try again.")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                callback.onCodeSent(verificationId, token)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phone) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout duration
            .setActivity(activity) // Activity for callback binding
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneNumber(credential: PhoneAuthCredential, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    fun registerEmailAndPassword(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    // If registration fails, delete the user if it was created
                    firebaseAuth.currentUser?.delete()
                    callback(false, task.exception?.message)
                }
            }
    }

    fun loginWithEmail(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }



    fun storeUserInRealtimeDatabase(user: User, callback: (Boolean, String?) -> Unit) {
        // Assuming User data can be converted to a Map or similar structure
        val userMap = user.toMap() // Implement this method based on your User data structure

        // Reference to the Realtime Database path where you want to store the user information
        val databaseReference = firebaseDatabase.getReference("Users").child(user.uuid)

        databaseReference.setValue(userMap)
            .addOnSuccessListener {
                callback(true, null)
            }
            .addOnFailureListener { e ->
                // If storing to Realtime Database fails, delete the user from authentication
                FirebaseAuth.getInstance().currentUser?.delete()
                callback(false, e.message)
            }


    }


    // Get the current user's UID from FirebaseAuth
    fun getCurrentUserUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }


}
