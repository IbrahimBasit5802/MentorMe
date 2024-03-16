package com.ibrahimbasit.I210669

data class Review(
    val rating : Float,
    val reviewText : String,
    val mentorId : String,
    val userId : String
) {
    constructor() : this(0.0f, "", "", "")
}
