package com.oyetech.composebase.projectQuestionsFeature.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

object AppSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
}

object AppCornerRadius {
    val none = 0.dp
    val xSmall = 6.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val xLarge = 20.dp
    val xxLarge = 24.dp
    val pill = 50.dp
}

object AppShapes {
    val roundedXSmall: Shape = RoundedCornerShape(AppCornerRadius.xSmall)
    val roundedSmall: Shape = RoundedCornerShape(AppCornerRadius.small)
    val roundedMedium: Shape = RoundedCornerShape(AppCornerRadius.medium)
    val roundedLarge: Shape = RoundedCornerShape(AppCornerRadius.large)
    val roundedXLarge: Shape = RoundedCornerShape(AppCornerRadius.xLarge)
    val roundedPill: Shape = RoundedCornerShape(AppCornerRadius.pill)
}

