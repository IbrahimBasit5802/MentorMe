package com.ibrahimbasit.I210669.auth.domain.use_cases

import com.google.firebase.auth.PhoneAuthCredential
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.models.User

class VerifyPhoneUseCase(
    private val authRepository: AuthRepository,
    private val name: String, // Store as a property
    private val contactNumber: String, // Store as a property
    private val country: String, // Store as a property
    private val city: String // Store as a property
) {
    fun verifyPhoneAndRegisterUser(credential: PhoneAuthCredential, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authRepository.verifyPhoneNumber(credential) { isSuccessful, errorMessage ->
            if (isSuccessful) {
                // Proceed to register email and password
                authRepository.registerEmailAndPassword(email, password) { isEmailRegisterSuccessful, emailErrorMessage ->
                    if (isEmailRegisterSuccessful) {
                        // Now we can create the User model without a password field
                        val userUuid = authRepository.getCurrentUserUid()
                        if (userUuid != null) {
                            val user = User(
                                name = name,
                                uuid = userUuid,
                                email = email,
                                contactNumber = contactNumber,
                                country = country,
                                city = city
                            )
                            // Proceed to store user in Firestore
                            authRepository.storeUserInRealtimeDatabase(user) { isStoredSuccessfully, DatabaseError ->
                                callback(isStoredSuccessfully, DatabaseError)
                            }
                        } else {
                            callback(false, "Unable to obtain user UUID")
                        }
                    } else {
                        callback(false, emailErrorMessage)
                    }
                }
            } else {
                callback(false, errorMessage)
            }
        }
    }
}
