package com.tenutz.contactsmessagesfetching.dto

data class Message(
    val messageId: String,
    val threadId: String,
    val address: String,
    val contactId: String,
    val contactIdString: String,
    val timeStamp: Long,
    val body: String,
)