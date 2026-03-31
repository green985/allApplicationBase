package com.oyetech.composebase.projectQuestionsFeature.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Raw palette — edit only here to change the brand design ──────────────────
private val RawBrandPrimary = Color(0xFFECA73B)
private val RawBrandPrimaryMuted = Color(0xFFF5D49A)

private val RawWhite = Color(0xFFFFFFFF)
private val RawNearWhite = Color(0xFFFAF9F7)
private val RawCream = Color(0xFFF2EDE5)
private val RawParchment = Color(0xFFE8E2D9)

private val RawCharcoal = Color(0xFF1A1917)
private val RawCharcoalSurface = Color(0xFF222120)
private val RawCharcoalElevated = Color(0xFF2C2A28)
private val RawCharcoalHigh = Color(0xFF3A3835)
private val RawMidTone = Color(0xFF6B6560)

private val RawErrorLight = Color(0xFFBA1A1A)
private val RawErrorDark = Color(0xFFFFB4AB)
private val RawDisabledLight = Color(0xFFCCC7C0)
private val RawDisabledDark = Color(0xFF5A5652)

// ── Internal palette contract — used by Theme.kt to build the M3 ColorScheme ─
internal data class AppColorPalette(
    val primary: Color,
    val onPrimary: Color,
    val primaryMuted: Color,
    val onPrimaryMuted: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryMuted: Color,
    val onSecondaryMuted: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryMuted: Color,
    val onTertiaryMuted: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceElevated: Color,
    val surfaceLowest: Color,
    val surfaceLow: Color,
    val surfaceHighest: Color,
    val surfaceDim: Color,
    val surfaceBright: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnPrimary: Color,
    val border: Color,
    val divider: Color,
    val error: Color,
    val onError: Color,
    val errorMuted: Color,
    val onErrorMuted: Color,
    val scrim: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val disabled: Color,
    val isLight: Boolean,
)

// ── Light palette ─────────────────────────────────────────────────────────────
internal val lightPalette = AppColorPalette(
    primary = RawBrandPrimary,
    onPrimary = RawWhite,
    primaryMuted = RawBrandPrimaryMuted,
    onPrimaryMuted = Color(0xFF3D2200),
    secondary = Color(0xFF6E5B3A),
    onSecondary = RawWhite,
    secondaryMuted = Color(0xFFF9E3B8),
    onSecondaryMuted = Color(0xFF271900),
    tertiary = Color(0xFF5A6340),
    onTertiary = RawWhite,
    tertiaryMuted = Color(0xFFDEE8BC),
    onTertiaryMuted = Color(0xFF181E04),
    background = RawWhite,
    onBackground = RawCharcoal,
    surface = RawNearWhite,
    onSurface = RawCharcoal,
    surfaceVariant = RawCream,
    onSurfaceVariant = RawMidTone,
    surfaceElevated = Color(0xFFECE7DF),
    surfaceLowest = RawWhite,
    surfaceLow = Color(0xFFF7F3EE),
    surfaceHighest = Color(0xFFE6E1D9),
    surfaceDim = Color(0xFFDDD8D3),
    surfaceBright = RawNearWhite,
    textPrimary = RawCharcoal,
    textSecondary = RawMidTone,
    textOnPrimary = RawWhite,
    border = RawParchment,
    divider = RawCream,
    error = RawErrorLight,
    onError = RawWhite,
    errorMuted = Color(0xFFFFDAD6),
    onErrorMuted = Color(0xFF93000A),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2F2E2C),
    inverseOnSurface = RawCream,
    inversePrimary = Color(0xFFFFB951),
    disabled = RawDisabledLight,
    isLight = true,
)

// ── Dark palette ──────────────────────────────────────────────────────────────
internal val darkPalette = AppColorPalette(
    primary = RawBrandPrimary,
    onPrimary = Color(0xFF3D2200),
    primaryMuted = Color(0xFF6B4E1A),
    onPrimaryMuted = Color(0xFFFFF0D0),
    secondary = Color(0xFFDCC69E),
    onSecondary = Color(0xFF3D2D0C),
    secondaryMuted = Color(0xFF554420),
    onSecondaryMuted = Color(0xFFF9E3B8),
    tertiary = Color(0xFFC3CCA0),
    onTertiary = Color(0xFF2C3416),
    tertiaryMuted = Color(0xFF434C2B),
    onTertiaryMuted = Color(0xFFDEE8BC),
    background = RawCharcoal,
    onBackground = Color(0xFFF0EBE4),
    surface = RawCharcoalSurface,
    onSurface = Color(0xFFF0EBE4),
    surfaceVariant = RawCharcoalHigh,
    onSurfaceVariant = Color(0xFFA89F98),
    surfaceElevated = RawCharcoalElevated,
    surfaceLowest = Color(0xFF141312),
    surfaceLow = Color(0xFF1E1D1B),
    surfaceHighest = Color(0xFF363432),
    surfaceDim = RawCharcoal,
    surfaceBright = RawCharcoalHigh,
    textPrimary = Color(0xFFF0EBE4),
    textSecondary = Color(0xFFA89F98),
    textOnPrimary = Color(0xFF3D2200),
    border = RawCharcoalHigh,
    divider = RawCharcoalElevated,
    error = RawErrorDark,
    onError = Color(0xFF690005),
    errorMuted = Color(0xFF93000A),
    onErrorMuted = Color(0xFFFFDAD6),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF0EBE4),
    inverseOnSurface = RawCharcoalHigh,
    inversePrimary = Color(0xFF825500),
    disabled = RawDisabledDark,
    isLight = false,
)

// ── CompositionLocal (internal — wired by RadioAppTheme) ─────────────────────
internal val LocalAppColors = staticCompositionLocalOf { lightPalette }

// ── Public API — use AppColors.primary directly in any @Composable ───────────
// Change the palette above; this object always reflects the active theme.
object AppColors {
    val primary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.primary

    val onPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.onPrimary

    val secondary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.secondary

    val tertiary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.tertiary

    val primaryMuted: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.primaryMuted

    val background: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.background

    val surface: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.surface

    val surfaceVariant: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.surfaceVariant

    val textPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.textPrimary

    val textSecondary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.textSecondary

    val textOnPrimary: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.textOnPrimary

    val border: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.border

    val divider: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.divider

    val error: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.error

    val disabled: Color
        @Composable @ReadOnlyComposable
        get() = LocalAppColors.current.disabled
}
