package com.ibrahimbasit.I210669

import java.io.Serializable

data class Mentor(
    val name: String,
    val role: String,
    val availability: Boolean,
    val profilePictureUrl: String,
    val price: Float,
    val description: String,
    val rating: Float
) : Serializable {
    constructor() : this("", "", false, "", 0.0f, "", 0.0f) // Include the null default for mentorId
}
