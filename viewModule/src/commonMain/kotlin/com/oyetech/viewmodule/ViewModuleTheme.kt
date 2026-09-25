package com.oyetech.viewmodule

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private fun buildLightColorScheme(palette: KmpColorPalette) = lightColorScheme(
    primary = palette.primary,
    onPrimary = palette.onPrimary,
    primaryContainer = palette.primaryContainer,
    onPrimaryContainer = palette.onPrimaryContainer,
    secondary = palette.secondary,
    onSecondary = palette.onSecondary,
    tertiary = palette.tertiary,
    onTertiary = palette.onTertiary,
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surfaceVariant,
    onSurfaceVariant = palette.onSurfaceVariant,
    error = palette.error,
    onError = palette.onError,
    errorContainer = palette.errorContainer,
    onErrorContainer = palette.onErrorContainer,
    outline = palette.outline,
    outlineVariant = palette.outlineVariant,
    inverseSurface = palette.inverseSurface,
    inverseOnSurface = palette.inverseOnSurface,
    inversePrimary = palette.inversePrimary,
)

private fun buildDarkColorScheme(palette: KmpColorPalette) = darkColorScheme(
    primary = palette.primary,
    onPrimary = palette.onPrimary,
    primaryContainer = palette.primaryContainer,
    onPrimaryContainer = palette.onPrimaryContainer,
    secondary = palette.secondary,
    onSecondary = palette.onSecondary,
    tertiary = palette.tertiary,
    onTertiary = palette.onTertiary,
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surfaceVariant,
    onSurfaceVariant = palette.onSurfaceVariant,
    error = palette.error,
    onError = palette.onError,
    errorContainer = palette.errorContainer,
    onErrorContainer = palette.onErrorContainer,
    outline = palette.outline,
    outlineVariant = palette.outlineVariant,
    inverseSurface = palette.inverseSurface,
    inverseOnSurface = palette.inverseOnSurface,
    inversePrimary = palette.inversePrimary,
)

@Composable
fun ViewModuleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            buildDarkColorScheme(kmpDarkPalette)
        } else {
            buildLightColorScheme(kmpLightPalette)
        },
        content = content,
    )
}
