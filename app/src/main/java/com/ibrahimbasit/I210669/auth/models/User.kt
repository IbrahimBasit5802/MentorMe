package com.ibrahimbasit.I210669.auth.models

import com.google.gson.annotations.SerializedName

data class User(
    var name: String,
    @SerializedName("uuid") val userId: String,
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
}
