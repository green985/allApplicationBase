package com.oyetech.models.sealeds.messagess

/**
Created by Erdi Özbek
-7.03.2022-
-22:18-
 **/

sealed class MessageTypeUtil {

    val type: String
        get() = when (this) {
            is Text -> "t"
            is Image -> "i"
            is Video -> "v"
            is Audio -> "a"
            is TextReply -> "r"
        }

    operator fun invoke(): String {
        return type
    }

    object Text : MessageTypeUtil()
    object Image : MessageTypeUtil()
    object Video : MessageTypeUtil()
    object Audio : MessageTypeUtil()
    object TextReply : MessageTypeUtil()
}



