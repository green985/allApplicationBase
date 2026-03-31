package com.oyetech.composebase.helpers.viewProperties

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideSubcomposition
import com.bumptech.glide.integration.compose.RequestState
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors

/**
Created by Erdi Özbek
-8.06.2025-
-17:34-
 **/

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GlideImageWithState(
    imageUrl: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderPainter: Painter? = null,
) {
    val context = LocalContext.current

    GlideSubcomposition(imageUrl, modifier) {
        when (state) {
            RequestState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.textPrimary)
                }
            }

            RequestState.Failure -> {
                placeholderPainter?.let {
                    Image(
                        painter = it,
                        contentDescription = "Placeholder",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            }

            is RequestState.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = contentScale,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
