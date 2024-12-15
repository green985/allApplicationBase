package com.oyetech.models.utils.states

/**
Created by Erdi Özbek
-14.11.2023-
-10:42-
 **/

sealed class ReadPageOrientation {

    val type: String
        get() = when (this) {
            VERTICAL -> "VERTICAL"
            HORIZONTAL -> "HORIZONTAL"
            else -> {
                "VERTICAL"
            }
        }

    operator fun invoke(): String {
        return type
    }

    object VERTICAL : ReadPageOrientation()
    object HORIZONTAL : ReadPageOrientation()
}
