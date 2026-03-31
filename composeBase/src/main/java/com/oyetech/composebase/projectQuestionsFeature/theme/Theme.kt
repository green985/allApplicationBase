package com.oyetech.composebase.projectQuestionsFeature.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// ── M3 ColorScheme builders ───────────────────────────────────────────────────
// Brand slots (primary, surface family, text, border) are read from the palette.
// Secondary/tertiary/inverse are neutral M3 derivatives and live only here.
@Suppress("MagicNumber")
private fun buildLightM3Scheme(p: AppColorPalette) = lightColorScheme(
    primary = p.primary,
    onPrimary = p.onPrimary,
    primaryContainer = p.primaryMuted,
    onPrimaryContainer = Color(0xFF3D2200),
    secondary = Color(0xFF6E5B3A),
    onSecondary = p.onPrimary,
    secondaryContainer = Color(0xFFF9E3B8),
    onSecondaryContainer = Color(0xFF271900),
    tertiary = Color(0xFF5A6340),
    onTertiary = p.onPrimary,
    tertiaryContainer = Color(0xFFDEE8BC),
    onTertiaryContainer = Color(0xFF181E04),
    error = p.error,
    onError = p.onPrimary,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    background = p.background,
    onBackground = p.textPrimary,
    surface = p.surface,
    onSurface = p.textPrimary,
    surfaceVariant = p.surfaceVariant,
    onSurfaceVariant = p.textSecondary,
    outline = p.border,
    outlineVariant = p.divider,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2F2E2C),
    inverseOnSurface = p.surfaceVariant,
    inversePrimary = Color(0xFFFFB951),
    surfaceDim = Color(0xFFDDD8D3),
    surfaceBright = p.surface,
    surfaceContainerLowest = p.background,
    surfaceContainerLow = Color(0xFFF7F3EE),
    surfaceContainer = p.surfaceVariant,
    surfaceContainerHigh = p.surfaceElevated,
    surfaceContainerHighest = Color(0xFFE6E1D9),
)

@Suppress("MagicNumber")
private fun buildDarkM3Scheme(p: AppColorPalette) = darkColorScheme(
    primary = p.primary,
    onPrimary = p.onPrimary,
    primaryContainer = p.primaryMuted,
    onPrimaryContainer = Color(0xFFFFF0D0),
    secondary = Color(0xFFDCC69E),
    onSecondary = Color(0xFF3D2D0C),
    secondaryContainer = Color(0xFF554420),
    onSecondaryContainer = Color(0xFFF9E3B8),
    tertiary = Color(0xFFC3CCA0),
    onTertiary = Color(0xFF2C3416),
    tertiaryContainer = Color(0xFF434C2B),
    onTertiaryContainer = Color(0xFFDEE8BC),
    error = p.error,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = p.background,
    onBackground = p.textPrimary,
    surface = p.surface,
    onSurface = p.textPrimary,
    surfaceVariant = p.surfaceVariant,
    onSurfaceVariant = p.textSecondary,
    outline = p.border,
    outlineVariant = p.divider,
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF0EBE4),
    inverseOnSurface = p.surfaceVariant,
    inversePrimary = Color(0xFF825500),
    surfaceDim = p.background,
    surfaceBright = Color(0xFF3A3835),
    surfaceContainerLowest = Color(0xFF141312),
    surfaceContainerLow = Color(0xFF1E1D1B),
    surfaceContainer = p.surface,
    surfaceContainerHigh = p.surfaceElevated,
    surfaceContainerHighest = Color(0xFF363432),
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
