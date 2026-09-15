package com.oyetech.kmpfeatures.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val RawBrandPrimary = Color(0xFFECA73B)
private val RawBrandPrimaryMuted = Color(0xFFF5D49A)
private val RawWhite = Color(0xFFFFFFFF)
private val RawNearWhite = Color(0xFFFAF9F7)
private val RawCream = Color(0xFFF2EDE5)
private val RawParchment = Color(0xFFE8E2D9)
private val RawCharcoal = Color(0xFF1A1917)
private val RawCharcoalSurface = Color(0xFF222120)
private val RawCharcoalHigh = Color(0xFF3A3835)
private val RawMidTone = Color(0xFF6B6560)
private val RawErrorLight = Color(0xFFBA1A1A)
private val RawErrorDark = Color(0xFFFFB4AB)

internal data class KmpColorPalette(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val outline: Color,
    val outlineVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
)

internal val kmpLightPalette = KmpColorPalette(
    primary = RawBrandPrimary,
    onPrimary = RawWhite,
    primaryContainer = RawBrandPrimaryMuted,
    onPrimaryContainer = Color(0xFF3D2200),
    secondary = Color(0xFF6E5B3A),
    onSecondary = RawWhite,
    tertiary = Color(0xFF5A6340),
    onTertiary = RawWhite,
    background = RawWhite,
    onBackground = RawCharcoal,
    surface = RawNearWhite,
    onSurface = RawCharcoal,
    surfaceVariant = RawCream,
    onSurfaceVariant = RawMidTone,
    error = RawErrorLight,
    onError = RawWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF93000A),
    outline = RawParchment,
    outlineVariant = RawCream,
    inverseSurface = Color(0xFF2F2E2C),
    inverseOnSurface = RawCream,
    inversePrimary = Color(0xFFFFB951),
)

internal val kmpDarkPalette = KmpColorPalette(
    primary = RawBrandPrimary,
    onPrimary = Color(0xFF3D2200),
    primaryContainer = Color(0xFF6B4E1A),
    onPrimaryContainer = Color(0xFFFFF0D0),
    secondary = Color(0xFFDCC69E),
    onSecondary = Color(0xFF3D2D0C),
    tertiary = Color(0xFFC3CCA0),
    onTertiary = Color(0xFF2C3416),
    background = RawCharcoal,
    onBackground = Color(0xFFF0EBE4),
    surface = RawCharcoalSurface,
    onSurface = Color(0xFFF0EBE4),
    surfaceVariant = RawCharcoalHigh,
    onSurfaceVariant = Color(0xFFA89F98),
    error = RawErrorDark,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline = RawCharcoalHigh,
    outlineVariant = Color(0xFF2C2A28),
    inverseSurface = Color(0xFFF0EBE4),
    inverseOnSurface = RawCharcoalHigh,
    inversePrimary = Color(0xFF825500),
)

internal val LocalKmpColors = staticCompositionLocalOf { kmpLightPalette }

object AppColors {
    val primary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.primary

    val onPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.onPrimary

    val primaryMuted: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.primaryContainer

    val secondary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.secondary

    val tertiary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.tertiary

    val background: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.background

    val surface: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.surface

    val surfaceVariant: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.surfaceVariant

    val textPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.onSurface

    val textSecondary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.onSurfaceVariant

    val textOnPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.onPrimary

    val error: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.error

    val border: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.outline

    val divider: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.outlineVariant

    val disabled: Color
        @Composable @ReadOnlyComposable
        get() = LocalKmpColors.current.onSurfaceVariant.copy(alpha = 0.38f)
}
