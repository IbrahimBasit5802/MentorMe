package com.ibrahimbasit.I210669.auth.domain.use_cases

import com.google.firebase.auth.PhoneAuthCredential
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.models.User

class VerifyPhoneUseCase(private val authRepository: AuthRepository, name: String, contactNumber : String, country : String, city : String) {

    fun verifyPhoneAndRegisterUser(credential: PhoneAuthCredential, email: String, password: String, callback: (Boolean, String?) -> Unit) {
        authRepository.verifyPhoneNumber(credential) { isSuccessful, errorMessage ->
            if (isSuccessful) {
                // Proceed to register email and password
                authRepository.registerEmailAndPassword(email, password) { isEmailRegisterSuccessful, emailErrorMessage ->
                    if (isEmailRegisterSuccessful) {
                        // Now we can create the User model without a password field
                        val user = authRepository.getCurrentUserUid()?.let {
                            User(
                                name = email, // This needs to be passed appropriately
                                uuid = it,
                                email = email,
                                contactNumber = "", // This needs to be passed appropriately
                                country = "", // This needs to be passed appropriately
                                city = "" // This needs to be passed appropriately
                            )
                        }
                        // Proceed to store user in Firestore
                        if (user != null) {
                            authRepository.storeUserInFirestore(user) { isStoredSuccessfully, firestoreErrorMessage ->
                                callback(isStoredSuccessfully, firestoreErrorMessage)
                            }
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
