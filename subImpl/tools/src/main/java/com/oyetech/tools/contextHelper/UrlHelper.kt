package com.oyetech.tools.contextHelper

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

object UrlHelper {

    /**
     * Opens a given [url] in an external browser or appropriate app.
     * This function is safe and avoids crashing if no activity is found.
     */
    fun openUrl(context: Context, url: String) {
        val normalizedUrl = normalizeUrl(url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizedUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val packageManager = context.packageManager
        if (intent.resolveActivity(packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Log error if needed
            Timber.w("No app found to handle URL: $normalizedUrl")
        }
    }

    /**
     * Ensures the URL starts with http or https.
     */
    private fun normalizeUrl(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }
    }
}