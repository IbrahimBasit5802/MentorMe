package com.ibrahimbasit.I210669

data class ChatSession(
    val id: String,
    val name: String, // Name of the mentor or user
    val lastMessage: String,
    val newMessagesCount: Int, // Number of new messages
    val profilePictureUrl: String // URL to profile image


) {
    constructor() : this("", "", "", 0, "")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "lastMessage" to lastMessage,
            "newMessagesCount" to newMessagesCount,
            "profilePictureUrl" to profilePictureUrl
        )
    }
}
