package com.oyetech.composebase.projectRadioFeature.views

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
Created by Erdi Özbek
-3.11.2024-
-11:37-
 **/
@Composable
fun AdViewHolder(
    modifier: Modifier = Modifier,
    adView: View,
    content: @Composable () -> Unit = {},
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            adView
        },
    )

    content()
}