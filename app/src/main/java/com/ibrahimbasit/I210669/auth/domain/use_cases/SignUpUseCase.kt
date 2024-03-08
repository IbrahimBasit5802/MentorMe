package com.ibrahimbasit.I210669.auth.domain.use_cases

import com.ibrahimbasit.I210669.auth.data.AuthRepository

class SignUpUseCase(private val authRepository: AuthRepository) {
    operator fun invoke(email: String, password: String, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        authRepository.signUp(email, password, name, onSuccess, onFailure)
    }
}
