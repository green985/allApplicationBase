package com.oyetech.composebase.projectRadioFeature.screens.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.tools.contextHelper.getApplicationLogo

/**
Created by Erdi Özbek
-24.11.2024-
-18:19-
 **/

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RadioLogoView(faviconUrl: String, size: Dp = RadioDimensions.radioLogoSmallWidthHeight) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .padding(start = 6.dp)
            .clip(RoundedCornerShape(RadioDimensions.radioTagCornerRadius))
    ) {
        GlideImage(
            model = faviconUrl,
            contentDescription = "RadioImage",
            failure = placeholder(context.getApplicationLogo()),
            modifier = Modifier
                .size(size),
        )
    }
}