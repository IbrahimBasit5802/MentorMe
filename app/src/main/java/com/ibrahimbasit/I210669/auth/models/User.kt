package com.ibrahimbasit.I210669.auth.models

data class User(
    var name: String,
    val uuid: String,
    var email: String,
    var contactNumber: String,
    var country: String,
    var city: String,
    var profilePictureUrl: String
) {

    constructor() : this("", "", "", "", "", "", "gs://i210669.appspot.com/user_profile_picture.png")


    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "uuid" to uuid,
            "email" to email,
            "contactNumber" to contactNumber,
            "country" to country,
            "city" to city,
            "profilePictureUrl" to profilePictureUrl
        )
    }
}
