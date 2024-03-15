package com.ibrahimbasit.I210669

data class Mentor(
    val name: String,
    val role : String,
    val availability : Boolean,
    val profilePictureUrl: String,
    val price : Float,
    val description : String,
    val rating : Float


) {
    constructor() : this("", "", false, "", 0.0f, "", 0.0f)
}
