package com.ibrahimbasit.I210669.auth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.ibrahimbasit.I210669.auth.domain.use_cases.VerifyPhoneUseCase
import com.ibrahimbasit.I210669.auth.models.User

class VerifyPhoneViewModel(private val verifyPhoneUseCase: VerifyPhoneUseCase) : ViewModel() {
    private val _verificationState = MutableLiveData<VerificationState>()
    val verificationState: LiveData<VerificationState> = _verificationState

    fun verifyPhoneWithCode(credential: PhoneAuthCredential, email : String, password : String) {
        verifyPhoneUseCase.verifyPhoneAndRegisterUser(credential, email, password) { isSuccess, message ->
            if (isSuccess) {
                _verificationState.postValue(VerificationState.Success)
            } else {
                _verificationState.postValue(VerificationState.Failure(message ?: "Unknown error"))
            }
        }
    }

    sealed class VerificationState {
        object Success : VerificationState()
        class Failure(val message: String) : VerificationState()
    }
}
