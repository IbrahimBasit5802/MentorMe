package com.ibrahimbasit.I210669.auth.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimbasit.I210669.auth.domain.use_cases.SignUpUseCase
import com.ibrahimbasit.I210669.auth.presentation.SignUpViewModel

class SignUpViewModelFactory(private val signUpUseCase: SignUpUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(signUpUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
