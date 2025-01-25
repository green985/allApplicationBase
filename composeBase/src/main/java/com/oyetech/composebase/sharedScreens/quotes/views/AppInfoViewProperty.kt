package com.oyetech.composebase.sharedScreens.quotes.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import com.oyetech.composebase.BuildConfig
import com.oyetech.composebase.projectRadioFeature.views.AboutAppView
import com.oyetech.tools.contextHelper.getAppName
import com.oyetech.tools.contextHelper.getApplicationLogo
import com.oyetech.tools.contextHelper.getVersionName

@Composable
fun AppInfoViewProperty() {
    val context = LocalContext.current
    val appLogo = context.getApplicationLogo()?.toBitmap()
        ?.asImageBitmap()// Replace with your app logo resource
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        AboutAppView(
            appName = context.getAppName(),
            appVersion = context.getVersionName(),
            isDebug = BuildConfig.DEBUG,
            logo = appLogo
        )
    }
}
