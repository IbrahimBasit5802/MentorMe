package com.ibrahimbasit.I210669

import java.io.Serializable

data class Mentor(
    var mentorId : String,
    val name: String,
    val role: String,
    val availability: Boolean,
    val profilePictureUrl: String,
    val price: Float,
    val description: String,
    val rating: Float,
    val chatSessions: Map<String, Boolean> = emptyMap(),
    var fcmToken: String? = null
// Map of chat session IDs to boolean (true if active)
) : Serializable {
    constructor() : this("", "", "", false, "", 0.0f, "", 0.0f)

    fun toMap(): Map<String, Any> {
        return mapOf(
            "mentorId" to mentorId,
            "name" to name,
            "role" to role,
            "availability" to availability,
            "profilePictureUrl" to profilePictureUrl,
            "price" to price,
            "description" to description,
            "rating" to rating,
            "chatSessions" to chatSessions
        )
    }
}
