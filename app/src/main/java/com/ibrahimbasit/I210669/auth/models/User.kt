package com.ibrahimbasit.I210669.auth.models

data class User(
    val name: String,
    val uuid: String,
    val email: String,
    val contactNumber: String,
    val country: String,
    val city: String,
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "uuid" to uuid,
            "email" to email,
            "contactNumber" to contactNumber,
            "country" to country,
            "city" to city
        )
    }
}
