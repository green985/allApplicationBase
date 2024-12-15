package com.oyetech.core.randomHelper

import java.util.Random
import java.util.UUID

/**
Created by Erdi Özbek
-25.05.2022-
-19:44-
 **/

object RandomHelper {

    fun getMinusRandomLong(): Long {
        var randomInt = getRandomNumber(500, 100000)

        return (-randomInt.toLong())
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt(max - min + 1) + min
    }

    fun generateConversationId(x: Long, y: Long): Long {
        return -(((x + y) * (x + y + 1)) / 2 + y)
    }

    fun getRandomInt(): Int {
        return (1233..12312333).random()
    }

    fun generateGuid(): String {
        val uniqueID: String = UUID.randomUUID().toString()
        return uniqueID
    }

    fun generateRandomStringForWallpaperSeed(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
