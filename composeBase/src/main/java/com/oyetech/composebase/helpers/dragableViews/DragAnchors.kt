package com.oyetech.composebase.helpers.dragableViews

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.R
import com.oyetech.composebase.helpers.dragableViews.DragAnchors.End
import com.oyetech.composebase.helpers.dragableViews.DragAnchors.Half
import com.oyetech.composebase.helpers.dragableViews.DragAnchors.OneQuarter
import com.oyetech.composebase.helpers.dragableViews.DragAnchors.Start
import com.oyetech.composebase.helpers.dragableViews.DragAnchors.ThreeQuarters
import timber.log.Timber
import kotlin.math.roundToInt

enum class DragAnchors(val fraction: Float) {
    Start(0f),
    OneQuarter(.25f),
    Half(.5f),
    ThreeQuarters(.75f),
    End(1f),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalDraggableSample(
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val positionalThreshold = { distance: Float -> distance * 1f }
    val velocityThreshold = { with(density) { 150.dp.toPx() } }
    val animationSpec = tween<Float>()
    val snapAnimationSpec = tween<Float>()
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    val state = rememberSaveable(
        density,
        saver = AnchoredDraggableState.Saver(
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = animationSpec,
            decayAnimationSpec = decayAnimationSpec,
        )
    ) {

        AnchoredDraggableState(
            initialValue = Start,
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = snapAnimationSpec,
            decayAnimationSpec = decayAnimationSpec,
        )
    }
    val contentSize = 80.dp
    val contentSizePx = with(density) { contentSize.toPx() }
    Box(
        modifier
            .onSizeChanged { layoutSize ->
                val dragEndPoint = layoutSize.width - contentSizePx
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors
                            .values()
                            .filterNot { anchor -> anchor == OneQuarter || anchor == Half || anchor == ThreeQuarters }
                            .forEach { anchor ->
                                anchor at dragEndPoint * anchor.fraction
                            }
                    }
                )
            }
    ) {
        DraggableContent(
            modifier = Modifier
                .size(contentSize)
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(),
                        y = 0,
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalDraggableSample(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    state: AnchoredDraggableState<DragAnchors>,
) {
    val density = LocalDensity.current

    val contentSize = 80.dp
    val contentSizePx = with(density) { contentSize.toPx() }
    Box(
        modifier
            .onSizeChanged { layoutSize ->
                val dragEndPoint = layoutSize.height - contentSizePx
                Timber.d("anchor. dragEndPoint  == " + dragEndPoint)
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors
                            .values()
                            .filterNot { anchor -> anchor == OneQuarter || anchor == Half || anchor == ThreeQuarters }
                            .forEach { anchor ->
                                Timber.d("anchor.fraction  == " + anchor.fraction)
                                anchor at dragEndPoint * anchor.fraction
                            }
                    }
                )
            }
    ) {
        content()

        DraggableContent(
            modifier = Modifier
                .fillMaxWidth()
                .size(contentSize)
                .offset {
                    IntOffset(
                        x = 0,
                        y = state
                            .requireOffset()
                            .roundToInt(),
                    )
                }
                .anchoredDraggable(state, Orientation.Vertical),
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun anchoredDraggableState(
    density: Density,
    positionalThreshold: (Float) -> Float,
    velocityThreshold: () -> Float,
    animationSpec: TweenSpec<Float>,
    decayAnimationSpec: DecayAnimationSpec<Float>,
    confirmValueChange: (DragAnchors) -> Boolean,

    ): AnchoredDraggableState<DragAnchors> {
    val state = rememberSaveable(
        density,
        saver = AnchoredDraggableState.Saver(
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = animationSpec,
            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = confirmValueChange
        )
    ) {
        AnchoredDraggableState(
            initialValue = End,
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            snapAnimationSpec = animationSpec,
            decayAnimationSpec = decayAnimationSpec,
        )
    }
    return state
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun makeStateForDrag(confirmValueChange: (DragAnchors) -> Boolean): AnchoredDraggableState<DragAnchors> {

    val density = LocalDensity.current
    val positionalThreshold = { distance: Float -> 160f }
    val velocityThreshold = { with(density) { 3000f } }
    val animationSpec =
        tween<Float>(durationMillis = 1000, delayMillis = 0, easing = LinearOutSlowInEasing)
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    return anchoredDraggableState(
        density = density,
        positionalThreshold = positionalThreshold,
        velocityThreshold = velocityThreshold,
        animationSpec = animationSpec,
        decayAnimationSpec = decayAnimationSpec,
        confirmValueChange,
    )
}

@Composable
fun DraggableContent(
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.error)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_android_black_24dp),
            contentDescription = null,
        )
    }
}