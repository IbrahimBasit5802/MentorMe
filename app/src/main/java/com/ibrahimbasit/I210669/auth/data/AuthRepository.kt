package com.ibrahimbasit.I210669.auth.data

import com.ibrahimbasit.I210669.auth.services.FirebaseAuthService

class AuthRepository(private val firebaseAuthService: FirebaseAuthService) {
    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseAuthService.signUp(email, password, name, onSuccess, onFailure)
    }
}
