package com.ibrahimbasit.I210669.auth.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimbasit.I210669.auth.domain.use_cases.LoginUseCase
import com.ibrahimbasit.I210669.auth.presentation.LoginViewModel

class LoginActivityViewModelFactory(private val loginUseCase : LoginUseCase) :ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(loginUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}