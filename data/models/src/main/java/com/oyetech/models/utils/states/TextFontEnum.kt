package com.oyetech.models.utils.states

import android.os.Parcelable
import androidx.annotation.Keep
import com.squareup.moshi.JsonClass

/**
Created by Erdi Özbek
-13.11.2023-
-21:47-
 **/

@Keep
@JsonClass(generateAdapter = true)
@kotlinx.parcelize.Parcelize
sealed class TextFontEnum : Parcelable {

    val type: String
        get() = when (this) {
            DEFAULT -> "DEFAULT"
            LATO -> "LATO"
            OPEN_SANS -> "OPEN_SANS"
            OSWALD -> "OSWALD"
            else -> {
                "DEFAULT"
            }
        }

    operator fun invoke(): String {
        return type
    }

    object DEFAULT : TextFontEnum()
    object OPEN_SANS : TextFontEnum()
    object LATO : TextFontEnum()
    object OSWALD : TextFontEnum()
}
