package com.oyetech.composebase.helpers.viewProperties

import androidx.compose.ui.window.DialogProperties

/**
Created by Erdi Özbek
-13.10.2024-
-22:50-
 **/
object DialogHelper {
    val fullScreenDialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = true
    )
    val fullScreenDialogPropertiesCanDismiss = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
        usePlatformDefaultWidth = false,
        decorFitsSystemWindows = true
    )
}