package com.oyetech.firebaseDB.firebaseDB.messaging

/**
Created by Erdi Özbek
-16.02.2025-
-15:39-
 **/

object FirebaseMessagingHelperFunctions {

    // with this function we are gonna create chat id with two user id
    fun generateChatId(userId1: String, userId2: String): String {
        return listOf(userId1, userId2).sorted().joinToString("_")
    }


}