package com.oyetech.core.shareOperations

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.widget.Toast

/**
Created by Erdi Özbek
-7.12.2023-
-13:27-
 **/

class ClipboardOperationHelper(private var context: Context) {

    fun copyToClipboardToWithClearText(
        string: String,
        replacingString: String,
        successMessage: String,
    ) {
        var tmpString = string.replace(replacingString, "")

        val clipboard: ClipboardManager? =
            context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("label", tmpString)
        clipboard?.setPrimaryClip(clip)

        if (successMessage.isNotBlank()) {

            Toast.makeText(this.context, successMessage, Toast.LENGTH_LONG).show()
        }
    }

    fun shareTextViaIntent(
        string: String,
        replacingString: String,
        clipboardTitleString: String,
    ) {
        var tmpString = string.replace(replacingString, "")

        shareTextWithIntent(tmpString, clipboardTitleString)
    }

    private fun shareTextWithIntent(urlToShare: String, titleString: String) {
        val intent2 = Intent()
        intent2.setAction(Intent.ACTION_SEND)
        intent2.setType("text/plain")
        intent2.putExtra(Intent.EXTRA_TEXT, urlToShare)
        context.startActivity(Intent.createChooser(intent2, titleString))
    }


}