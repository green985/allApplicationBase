import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.helpers.viewProperties.toPx
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun EqualizerView(
    active: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,        // Çubuk sayısı
    barWidth: Dp = 10.dp,      // Çubuk genişliği (dp cinsinden)
    maxHeight: Dp = 100.dp,    // Çubuk maksimum yüksekliği (dp cinsinden)
    barStartHeight: Dp = 40.dp, // Çubuk başlangıç yüksekliği (dp cinsinden)
    color: Color = Color.Green, // Çubuk rengi
) {
    // Her çubuk için başlangıç yüksekliğini belirliyoruz
    val initialHeights = List(barCount) { barStartHeight.toPx() }

    // Her çubuk için animasyon değerleri
    val animatedHeights = List(barCount) { index ->
        remember { Animatable(initialHeights[index]) }
    }
    val maxHeightFloat = maxHeight.toPx()
    LaunchedEffect(active) {
        if (active) {
            // Her çubuk için animasyonları başlat
            animatedHeights.forEach { animatable ->
                launch {
                    while (active) {
                        val targetHeight = Random.nextFloat() * maxHeightFloat
                        animatable.animateTo(
                            targetValue = targetHeight,
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = LinearEasing
                            )
                        )
                    }
                }
            }
        } else {
            // `active` false olduğunda çubuklar sabit başlangıç yüksekliklerine döner
            animatedHeights.forEachIndexed { index, animatable ->
                launch {
                    animatable.animateTo(
                        targetValue = initialHeights[index],
                        animationSpec = tween(durationMillis = 300)
                    )
                }
            }
        }
    }

    // Çubukları çizen Canvas
    Row(modifier = modifier) {
        animatedHeights.forEach { animatable ->
            Canvas(
                modifier = Modifier
                    .width(barWidth)
                    .height(maxHeight)
            ) {
                drawRect(
                    color = color,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = 0f,
                        y = size.height - animatable.value // Çubuğun en altından yukarıya doğru uzaması için
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = barWidth.toPx(),
                        height = animatable.value
                    )
                )
            }
        }
    }
}
