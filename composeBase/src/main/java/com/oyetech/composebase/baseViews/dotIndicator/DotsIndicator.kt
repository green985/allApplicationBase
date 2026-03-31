package com.oyetech.composebase.baseViews.dotIndicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    dotSpacing: Dp = 4.dp,
    selectedColor: Color = AppColors.primary,
    unSelectedColor: Color = AppColors.textPrimary.copy(alpha = 0.3f),
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = dotSpacing / 2)
                    .size(dotSize)
                    .background(
                        color = if (index == selectedIndex) selectedColor else unSelectedColor,
                        shape = CircleShape
                    )

            )
        }
    }
}

@Composable
fun DotsIndicatorSmallAnim(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    selectedDotSize: Dp = 16.dp,
    dotSpacing: Dp = 4.dp,
    selectedColor: Color = AppColors.primary,
    unSelectedColor: Color = AppColors.textPrimary.copy(alpha = 0.3f),
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex

            // Animasyonlu renk geçişi
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) selectedColor else unSelectedColor,
                animationSpec = tween(durationMillis = 300)
            )

            // Animasyonlu boyut geçişi
            val animatedSize by animateDpAsState(
                targetValue = if (isSelected) selectedDotSize else dotSize,
                animationSpec = tween(durationMillis = 300)
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = dotSpacing / 2)
                    .size(animatedSize)
                    .background(
                        color = animatedColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun DotsIndicatorPulseAnim(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    dotSize: Dp = 12.dp,
    dotSpacing: Dp = 6.dp,
    selectedColor: Color = AppColors.primary,
    unSelectedColor: Color = AppColors.textPrimary.copy(alpha = 0.3f),
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalDots) { index ->
            val isSelected = index == selectedIndex

            val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")

            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            val scale = if (isSelected) pulseScale else 1f
            val color = if (isSelected) selectedColor else unSelectedColor

            Box(
                modifier = Modifier
                    .padding(horizontal = dotSpacing / 2)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .size(dotSize)
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}
