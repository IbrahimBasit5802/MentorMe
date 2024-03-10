package com.ibrahimbasit.I210669.auth.domain.use_cases

import com.ibrahimbasit.I210669.auth.data.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {

    fun execute(email: String, password: String, callback : (Boolean, String) -> Unit) {
        repository.loginWithEmail(email, password) { isSuccess, message ->
            if (isSuccess) {
                callback(true, "")
            } else {
                callback(false, message ?: "Unknown error")
            }
        }
    }
}

