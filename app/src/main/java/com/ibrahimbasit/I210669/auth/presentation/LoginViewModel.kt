package com.ibrahimbasit.I210669.auth.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibrahimbasit.I210669.auth.domain.use_cases.LoginUseCase

class LoginViewModel (private val loginUseCase: LoginUseCase) :ViewModel() {

    private val _userLoginStatus = MutableLiveData<LoginStatus>()
    val userLoginStatus: LiveData<LoginStatus> = _userLoginStatus

    fun login(email: String, password: String) {
        loginUseCase.execute(email, password) { isSuccess, message ->
            if (isSuccess) {
                _userLoginStatus.postValue(LoginStatus.LoginSuccessful)
            } else {
                _userLoginStatus.postValue(LoginStatus.LoginFailed(message ?: "Unknown error"))
            }
        }
    }


    sealed class LoginStatus {
        data object LoginSuccessful : LoginStatus()
        class LoginFailed(val error: String) : LoginStatus()
    }
}