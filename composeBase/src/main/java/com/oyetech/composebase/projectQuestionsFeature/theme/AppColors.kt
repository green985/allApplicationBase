package com.oyetech.composebase.projectQuestionsFeature.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Raw palette — brand-anchored, warm and restrained
// ---------------------------------------------------------------------------
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

// ---------------------------------------------------------------------------
// Semantic color contract
// ---------------------------------------------------------------------------
data class AppColors(
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

// ---------------------------------------------------------------------------
// Light palette
// ---------------------------------------------------------------------------
val AppColorsLight = AppColors(
    primary = RawBrandPrimary,
    onPrimary = RawWhite,
    primaryMuted = RawBrandPrimaryMuted,
    background = RawWhite,
    surface = RawNearWhite,
    surfaceVariant = RawCream,
    surfaceElevated = RawWhite,
    textPrimary = RawCharcoal,
    textSecondary = RawMidTone,
    textOnPrimary = RawWhite,
    border = RawParchment,
    divider = RawCream,
    error = RawErrorLight,
    disabled = RawDisabledLight,
    isLight = true,
)

// ---------------------------------------------------------------------------
// Dark palette
// ---------------------------------------------------------------------------
val AppColorsDark = AppColors(
    primary = RawBrandPrimary,
    onPrimary = RawCharcoal,
    primaryMuted = Color(0xFF6B4E1A),
    background = RawCharcoal,
    surface = RawCharcoalSurface,
    surfaceVariant = RawCharcoalElevated,
    surfaceElevated = RawCharcoalHigh,
    textPrimary = Color(0xFFF0EBE4),
    textSecondary = Color(0xFFA89F98),
    textOnPrimary = RawCharcoal,
    border = RawCharcoalHigh,
    divider = RawCharcoalElevated,
    error = RawErrorDark,
    disabled = RawDisabledDark,
    isLight = false,
)

// ---------------------------------------------------------------------------
// CompositionLocal + MaterialTheme extension
// ---------------------------------------------------------------------------
val LocalAppColors = staticCompositionLocalOf { AppColorsLight }

val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

