package com.ibrahimbasit.I210669.auth.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibrahimbasit.I210669.auth.domain.use_cases.VerifyPhoneUseCase
import com.ibrahimbasit.I210669.auth.presentation.VerifyPhoneViewModel

class VerifyPhoneViewModelFactory(private val verifyPhoneUseCase: VerifyPhoneUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerifyPhoneViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VerifyPhoneViewModel(verifyPhoneUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
