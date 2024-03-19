package com.ibrahimbasit.I210669

data class ChatSession(
    val id: String,
    val mentorId: String, // ID of the mentor
    val userId: String,
    val mentorName: String, // Name of the mentor or user
    val userName: String,
    val lastMessage: String,
    val lastMessageSender : String,
    val newMessagesCount: Int, // Number of new messages
    val mentorProfilePictureUrl: String, // URL to profile image
    val userProfilePictureUrl : String


) {
    constructor() : this("", "", "","", "","", "", 0, "","")

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "mentorId" to id,
            "userId" to userId,
            "mentorName" to mentorName,
            "userName" to userName,
            "lastMessage" to lastMessage,
            "lastMessageSender" to lastMessageSender, // "Mentor" or "User"
            "newMessagesCount" to newMessagesCount,
            "mentorProfilePictureUrl" to mentorProfilePictureUrl,
            "userProfilePictureUrl" to userProfilePictureUrl
        )
    }
}
