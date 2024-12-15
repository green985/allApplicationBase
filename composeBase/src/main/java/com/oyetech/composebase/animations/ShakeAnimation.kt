import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

@Composable
fun ShakeAnimation(
    modifier: Modifier = Modifier,
    isEnable: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Animation properties
    val offset by animateDpAsState(
        targetValue = if (isEnable) 4.dp else (-4).dp, // 4 dp sola veya sağa
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 50), // Kısa süreli
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .offset(x = if (isEnable) offset else 0.dp) // Yatay kayma
            .alpha(if (isEnable) 1f else 1f) // İsteğe bağlı görünürlük efekti
    ) {
        content()
    }
}
