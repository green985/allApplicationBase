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
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textOnPrimary: Color,
    val border: Color,
    val divider: Color,
    val error: Color,
    val disabled: Color,
    val isLight: Boolean,
)

// ── Light palette ─────────────────────────────────────────────────────────────
internal val lightPalette = AppColorPalette(
    primary = RawBrandPrimary,
    onPrimary = RawWhite,
    primaryMuted = RawBrandPrimaryMuted,
    background = RawWhite,
    surface = RawNearWhite,
    surfaceVariant = RawCream,
    surfaceElevated = Color(0xFFECE7DF),
    textPrimary = RawCharcoal,
    textSecondary = RawMidTone,
    textOnPrimary = RawWhite,
    border = RawParchment,
    divider = RawCream,
    error = RawErrorLight,
    disabled = RawDisabledLight,
    isLight = true,
)

// ── Dark palette ──────────────────────────────────────────────────────────────
internal val darkPalette = AppColorPalette(
    primary = RawBrandPrimary,
    onPrimary = Color(0xFF3D2200),
    primaryMuted = Color(0xFF6B4E1A),
    background = RawCharcoal,
    surface = RawCharcoalSurface,
    surfaceVariant = RawCharcoalHigh,
    surfaceElevated = RawCharcoalElevated,
    textPrimary = Color(0xFFF0EBE4),
    textSecondary = Color(0xFFA89F98),
    textOnPrimary = Color(0xFF3D2200),
    border = RawCharcoalHigh,
    divider = RawCharcoalElevated,
    error = RawErrorDark,
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
