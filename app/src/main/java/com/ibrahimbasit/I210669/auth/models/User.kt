package com.ibrahimbasit.I210669.auth.models

data class User(
    var name: String,
    val uuid: String,
    var email: String,
    var contactNumber: String,
    var country: String,
    var city: String,
    var profilePictureUrl: String,
    var coverPhotoUrl: String,
    var chatSessions: Map<String, Boolean> = emptyMap(),
    var fcmToken: String? = null
// Map of chat session IDs to boolean (true if active)
) {
    constructor() : this("", "", "", "", "", "", "gs://i210669.appspot.com/user_profile_picture.png", "gs://i210669.appspot.com/over_photo.png")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "uuid" to uuid,
            "email" to email,
            "contactNumber" to contactNumber,
            "country" to country,
            "city" to city,
            "profilePictureUrl" to profilePictureUrl,
            "coverPhotoUrl" to coverPhotoUrl,
            "chatSessions" to chatSessions
        )
    }
}
