package com.oyetech.tools.stringHelper

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.webkit.URLUtil
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID

/**
Created by Erdi Özbek
-2.03.2022-
-18:31-
 **/

object StringHelper {

    fun getApplicationName(context: Context): String? {
        val stringId = context.applicationInfo.labelRes
        return context.getString(stringId)
    }

    fun String.toMD5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    fun String.toUniqString(): String {
        return this + generateRandomGuid()
    }

    fun generateRandomGuid(): String {
        return UUID.randomUUID().toString()
    }

    fun base64ToByteArray(base64String: String?): ByteArray? {
        if (base64String.isNullOrBlank()) {
            Timber.d("base64String null")
            return null
        }

        var pureBase64Encoded = base64String.substring(base64String.indexOf(",") + 1)
        var byteArray = Base64.decode(pureBase64Encoded, Base64.DEFAULT)
        return byteArray
    }

    fun String.languageValueOf(args: String?): String {
        if (args.isNullOrBlank()) return ""
        return String.format(this, args)
    }

    fun String.formatWithMultiple(vararg args: Any?): String {
        return String.format(this, args)
    }

    fun String.languageValueOf(args: Int?): String {
        return String.format(this, args)
    }

    fun String.isValidUrl(): Boolean {
        return URLUtil.isValidUrl(this)
    }

    fun removeStringSpanFromSearchView(
        s: String?,
    ): String {
        if (s.isNullOrEmpty()) return ""
        var stringg = s.replace("\n", "")
        val spanned = SpannableString(stringg)
        var queryText = removeSpan(spanned)
        return queryText ?: ""
    }

    private fun removeSpan(s: Spannable): String? {
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s.toString()
    }

    fun makeResolutionTextWithSystemProperty(systemResolution: Pair<Int, Int>): String {
        return "" + systemResolution.first + " x " + systemResolution.second
    }

    fun getColorString(colorInt: Int): String {
        val hexColor = java.lang.String.format("#%06X", 0xFFFFFF and colorInt)
        return hexColor
    }
}
