package com.oyetech.composebase.navigator

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import timber.log.Timber

@Keep
@Serializable
data class Route(val route: String) {
    fun withArgs(vararg args: Pair<String, String?>): String {
        val buildString = buildString {
            append(route)
            if (args.isNotEmpty()) {
                append("?")
                append(
                    args.filter { it.second != null }
                        .joinToString("&") { "${it.first}=${it.second}" }
                )
            }
        }
        Timber.d(" Route: $buildString")
        return buildString
    }


}