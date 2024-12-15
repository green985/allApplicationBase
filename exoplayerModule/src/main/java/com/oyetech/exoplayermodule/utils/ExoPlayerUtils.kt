package com.oyetech.exoplayermodule.utils

import java.util.regex.Pattern

/**
Created by Erdi Özbek
-19.11.2022-
-00:10-
 **/

fun urlIndicatesHlsStream(streamUrl: String?): Boolean {
    val p = Pattern.compile(".*\\.m3u8([#?\\s].*)?$")
    return p.matcher(streamUrl).matches()
}