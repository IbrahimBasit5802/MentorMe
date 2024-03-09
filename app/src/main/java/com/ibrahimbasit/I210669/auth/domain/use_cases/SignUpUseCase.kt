package com.ibrahimbasit.I210669.auth.domain.use_cases

import androidx.appcompat.app.AppCompatActivity
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.google.firebase.auth.PhoneAuthProvider

class SignUpUseCase(private val repository: AuthRepository) {

    fun execute(
        phone: String,
        email: String,
        password: String,
        name: String,
        country: String,
        activity: AppCompatActivity, // Added parameter for AppCompatActivity
        callback: AuthRepository.Callback // Added parameter for callback
    ) {
        repository.signUpWithPhone(phone, email, password, name, country, activity, object : AuthRepository.Callback {
            override fun onVerificationCompleted() {
                callback.onVerificationCompleted()
            }

            override fun onVerificationFailed(error: String) {
                callback.onVerificationFailed(error)
            }

            // Correct the signature to include all required parameters
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                callback.onCodeSent(verificationId, token)
            }
        })
    }
}
