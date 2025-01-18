package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.runtime.Composable
import com.oyetech.composebase.helpers.general.GeneralSettings

/**
Created by Erdi Özbek
-18.01.2025-
-11:17-
 **/

object AllScreenNavigationHelper {

    @Composable
    fun checkAndNavigate(
        normalNavigateProcess: @Composable (() -> Unit),
        debugNavigateProcess: @Composable (() -> Unit),
    ) {
        if (GeneralSettings.isDebug()) {
            debugNavigateProcess()
        } else {
            normalNavigateProcess()
        }
    }
}
