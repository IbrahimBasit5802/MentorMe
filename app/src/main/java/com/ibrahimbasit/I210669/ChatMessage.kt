package com.ibrahimbasit.I210669

data class ChatMessage(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val message: String,
    val timestamp: Long,
    val type: String, // text, image, voice, file
    val mediaUrl: String? = null,
    val isEdited: Boolean = false,
    val isDeleted: Boolean = false
) {
    constructor() : this("", "", "", "", -1, "text")
}
