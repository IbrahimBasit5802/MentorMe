package com.ibrahimbasit.I210669

data class Booking(
    val bookingId: String,
    val mentorId: String,
    val userId: String,
    val sessionPrice : String,
    val date: String,
    val time: String,
    val duration: String,
    val status: String
) {
    constructor() : this("","", "", "", "", "", "", "")
}
