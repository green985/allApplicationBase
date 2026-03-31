package com.oyetech.composebase.projectQuestionsFeature.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// ── M3 ColorScheme builders ───────────────────────────────────────────────────
// Brand slots (primary, surface family, text, border) are read from the palette.
// Secondary/tertiary/inverse are neutral M3 derivatives and live only here.
private fun buildLightM3Scheme(p: AppColorPalette) = lightColorScheme(
    primary = p.primary,
    onPrimary = p.onPrimary,
    primaryContainer = p.primaryMuted,
    onPrimaryContainer = p.onPrimaryMuted,
    secondary = p.secondary,
    onSecondary = p.onSecondary,
    secondaryContainer = p.secondaryMuted,
    onSecondaryContainer = p.onSecondaryMuted,
    tertiary = p.tertiary,
    onTertiary = p.onTertiary,
    tertiaryContainer = p.tertiaryMuted,
    onTertiaryContainer = p.onTertiaryMuted,
    error = p.error,
    onError = p.onError,
    errorContainer = p.errorMuted,
    onErrorContainer = p.onErrorMuted,
    background = p.background,
    onBackground = p.onBackground,
    surface = p.surface,
    onSurface = p.onSurface,
    surfaceVariant = p.surfaceVariant,
    onSurfaceVariant = p.onSurfaceVariant,
    outline = p.border,
    outlineVariant = p.divider,
    scrim = p.scrim,
    inverseSurface = p.inverseSurface,
    inverseOnSurface = p.inverseOnSurface,
    inversePrimary = p.inversePrimary,
    surfaceDim = p.surfaceDim,
    surfaceBright = p.surfaceBright,
    surfaceContainerLowest = p.surfaceLowest,
    surfaceContainerLow = p.surfaceLow,
    surfaceContainer = p.surfaceVariant,
    surfaceContainerHigh = p.surfaceElevated,
    surfaceContainerHighest = p.surfaceHighest,
)

private fun buildDarkM3Scheme(p: AppColorPalette) = darkColorScheme(
    primary = p.primary,
    onPrimary = p.onPrimary,
    primaryContainer = p.primaryMuted,
    onPrimaryContainer = p.onPrimaryMuted,
    secondary = p.secondary,
    onSecondary = p.onSecondary,
    secondaryContainer = p.secondaryMuted,
    onSecondaryContainer = p.onSecondaryMuted,
    tertiary = p.tertiary,
    onTertiary = p.onTertiary,
    tertiaryContainer = p.tertiaryMuted,
    onTertiaryContainer = p.onTertiaryMuted,
    error = p.error,
    onError = p.onError,
    errorContainer = p.errorMuted,
    onErrorContainer = p.onErrorMuted,
    background = p.background,
    onBackground = p.onBackground,
    surface = p.surface,
    onSurface = p.onSurface,
    surfaceVariant = p.surfaceVariant,
    onSurfaceVariant = p.onSurfaceVariant,
    outline = p.border,
    outlineVariant = p.divider,
    scrim = p.scrim,
    inverseSurface = p.inverseSurface,
    inverseOnSurface = p.inverseOnSurface,
    inversePrimary = p.inversePrimary,
    surfaceDim = p.surfaceDim,
    surfaceBright = p.surfaceBright,
    surfaceContainerLowest = p.surfaceLowest,
    surfaceContainerLow = p.surfaceLow,
    surfaceContainer = p.surface,
    surfaceContainerHigh = p.surfaceElevated,
    surfaceContainerHighest = p.surfaceHighest,
)

// Consumed by RadioAppTheme — update AppColors.kt to change the design.
val appLightColorScheme = buildLightM3Scheme(lightPalette)
val appDarkColorScheme = buildDarkM3Scheme(darkPalette)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme = ColorFamily(
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
    Color.Unspecified,
)
