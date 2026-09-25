package com.oyetech.kmpfeatures.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val kmpLightColorScheme = lightColorScheme()
private val kmpDarkColorScheme = darkColorScheme()

@Composable
fun KmpFeaturesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) kmpDarkColorScheme else kmpLightColorScheme,
        content = content,
    )
}
