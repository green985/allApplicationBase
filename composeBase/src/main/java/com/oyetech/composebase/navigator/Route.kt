package com.oyetech.composebase.navigator

import timber.log.Timber

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