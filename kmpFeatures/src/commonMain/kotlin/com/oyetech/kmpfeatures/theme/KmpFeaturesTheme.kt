package com.oyetech.kmpfeatures.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val kmpLightColorScheme = lightColorScheme(
    primary = kmpLightPalette.primary,
    onPrimary = kmpLightPalette.onPrimary,
    primaryContainer = kmpLightPalette.primaryContainer,
    onPrimaryContainer = kmpLightPalette.onPrimaryContainer,
    secondary = kmpLightPalette.secondary,
    onSecondary = kmpLightPalette.onSecondary,
    tertiary = kmpLightPalette.tertiary,
    onTertiary = kmpLightPalette.onTertiary,
    background = kmpLightPalette.background,
    onBackground = kmpLightPalette.onBackground,
    surface = kmpLightPalette.surface,
    onSurface = kmpLightPalette.onSurface,
    surfaceVariant = kmpLightPalette.surfaceVariant,
    onSurfaceVariant = kmpLightPalette.onSurfaceVariant,
    error = kmpLightPalette.error,
    onError = kmpLightPalette.onError,
    errorContainer = kmpLightPalette.errorContainer,
    onErrorContainer = kmpLightPalette.onErrorContainer,
    outline = kmpLightPalette.outline,
    outlineVariant = kmpLightPalette.outlineVariant,
    inverseSurface = kmpLightPalette.inverseSurface,
    inverseOnSurface = kmpLightPalette.inverseOnSurface,
    inversePrimary = kmpLightPalette.inversePrimary,
)

private val kmpDarkColorScheme = darkColorScheme(
    primary = kmpDarkPalette.primary,
    onPrimary = kmpDarkPalette.onPrimary,
    primaryContainer = kmpDarkPalette.primaryContainer,
    onPrimaryContainer = kmpDarkPalette.onPrimaryContainer,
    secondary = kmpDarkPalette.secondary,
    onSecondary = kmpDarkPalette.onSecondary,
    tertiary = kmpDarkPalette.tertiary,
    onTertiary = kmpDarkPalette.onTertiary,
    background = kmpDarkPalette.background,
    onBackground = kmpDarkPalette.onBackground,
    surface = kmpDarkPalette.surface,
    onSurface = kmpDarkPalette.onSurface,
    surfaceVariant = kmpDarkPalette.surfaceVariant,
    onSurfaceVariant = kmpDarkPalette.onSurfaceVariant,
    error = kmpDarkPalette.error,
    onError = kmpDarkPalette.onError,
    errorContainer = kmpDarkPalette.errorContainer,
    onErrorContainer = kmpDarkPalette.onErrorContainer,
    outline = kmpDarkPalette.outline,
    outlineVariant = kmpDarkPalette.outlineVariant,
    inverseSurface = kmpDarkPalette.inverseSurface,
    inverseOnSurface = kmpDarkPalette.inverseOnSurface,
    inversePrimary = kmpDarkPalette.inversePrimary,
)

@Composable
fun KmpFeaturesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val palette = if (darkTheme) kmpDarkPalette else kmpLightPalette
    val colorScheme = if (darkTheme) kmpDarkColorScheme else kmpLightColorScheme

    CompositionLocalProvider(LocalKmpColors provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
