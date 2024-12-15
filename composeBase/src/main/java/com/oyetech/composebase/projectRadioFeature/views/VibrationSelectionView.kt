package com.oyetech.composebase.projectRadioFeature.views

import ShakeAnimation
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.R
import com.oyetech.composebase.helpers.vibrationHelper.VibrationType
import com.oyetech.composebase.helpers.vibrationHelper.VibrationType.RAIN

@Composable
fun VibrationSelectionView(
    modifier: Modifier = Modifier,
    type: VibrationType,
    isEnable: Boolean = false,
    size: Dp = 80.dp,  // Boyutu dışarıdan belirlemek için Dp tipi
) {
    val imageRes = getImageResFromVibrationType(type)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = modifier
                .size(size)  // Boyut ayarı
                .clip(CircleShape)  // Dairesel görünüm
                .background(
                    if (isEnable) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.inverseOnSurface
                    }
                ),  // Tıklama olayı
            contentAlignment = Alignment.Center
        ) {
            // Dairesel Görüntü

            ShakeAnimation(isEnable = isEnable) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = type.name,
                    modifier = Modifier
                        .size(size * 0.8f)  // Görüntü boyutunu kutunun %80'i yapalım
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp)) // 16 dp yüksekliğinde boşluk

        Text(
            text = type.name,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun getImageResFromVibrationType(type: VibrationType): Int {
    var imageRes = R.drawable.baseline_vibration_24


    return imageRes
}

// preview yaz
@Preview
@Composable
fun VibrationSelectionViewPreview() {
    VibrationSelectionView(
        type = RAIN,
        isEnable = false,
    )
}

// preview yaz
@Preview
@Composable
fun VibrationSelectionViewPreviewTrue() {
    VibrationSelectionView(
        type = RAIN,
        isEnable = true,
    )
}
