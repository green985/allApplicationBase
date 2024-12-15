package com.oyetech.core.appBrowser

import android.content.Context
import android.content.Intent
import com.oyetech.models.utils.const.ActivityNameConst

/**
Created by Erdi Özbek
-26.06.2022-
-21:03-
 **/

var BROWSER_URL_STRING: String? = "BROWSER_URL_STRING"
fun startAppBrowserWithUrlCore(context: Context, urlString: String) {
    var className = ActivityNameConst.appBrowserActivityName
    var intent = Intent()
    intent.putExtra(BROWSER_URL_STRING, urlString)
    intent.setClassName(context.packageName, className)
    context.startActivity(intent)
}
