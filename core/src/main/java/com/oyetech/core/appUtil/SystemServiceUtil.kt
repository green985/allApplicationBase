package com.oyetech.core.appUtil

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.oyetech.core.contextHelper.getAppName

/**
Created by Erdi Özbek
-24.08.2022-
-22:21-
 **/

fun Context.copyClipboard(selectedText: String) {
    val clipboard: ClipboardManager? =
        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(this.getAppName(), selectedText)
    if (clipboard == null || clip == null) return
    clipboard.setPrimaryClip(clip)
}
