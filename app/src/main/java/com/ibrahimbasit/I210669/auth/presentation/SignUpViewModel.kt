package com.ibrahimbasit.I210669.auth.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthProvider
import com.ibrahimbasit.I210669.auth.data.AuthRepository
import com.ibrahimbasit.I210669.auth.domain.use_cases.SignUpUseCase

class SignUpViewModel(private val signUpUseCase: SignUpUseCase) : ViewModel() {
    private val _userSignUpStatus = MutableLiveData<SignUpStatus>()
    val userSignUpStatus: LiveData<SignUpStatus> = _userSignUpStatus

    fun signUpWithPhone(phone: String, email: String, password: String, name: String, country: String, activity: AppCompatActivity) {
        signUpUseCase.execute(phone, email, password, name, country, activity, object : AuthRepository.Callback {
            override fun onVerificationCompleted() {
                _userSignUpStatus.postValue(SignUpStatus.VerificationCompleted)
            }

            override fun onVerificationFailed(error: String) {
                _userSignUpStatus.postValue(SignUpStatus.VerificationFailed(error))
            }

            override fun onCodeSent(token: String, resendingToken: PhoneAuthProvider.ForceResendingToken) {
                // Assuming you want to pass just the verificationId to the LiveData
                _userSignUpStatus.postValue(SignUpStatus.CodeSent(token))
            }
        })
    }

    sealed class SignUpStatus {
        data object VerificationCompleted : SignUpStatus()
        class VerificationFailed(val error: String) : SignUpStatus()
        class CodeSent(val token: String) : SignUpStatus()
    }
}
