package com.oyetech.composebase.experimental.moonOperation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.base.BaseScaffold
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-8.07.2025-
-00:17-
 **/

@Composable
fun MoonOperationScreenSetup(viewModel: MoonOperationVm = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    MoonOperationScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )

}

@Suppress("FunctionName")
@Composable
fun MoonOperationScreen(
    modifier: Modifier = Modifier,
    uiState: MoonOperationUiState,
    onEvent: (MoonOperationEvent) -> (Unit),
) {
    BaseScaffold {
        Column(modifier = Modifier.padding()) {

            LaunchedEffect(Unit) { onEvent(MoonOperationEvent.Moon) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                uiState.illuminationPercent?.let {
                    val normalized = it / 100.0
                    Text("Aydınlanma: $it%")
                    Spacer(Modifier.height(8.dp))
                    MoonPhaseVisual(illumination = normalized)
                }


                if (uiState.error != null) {
                    Text(text = "Hata: ${uiState.error}", color = Color.Red)
                } else {
                    Text(
                        text = uiState.moonName,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Faz: ${uiState.phaseName}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    uiState.illuminationPercent?.let {
                        Text("Aydınlanma: $it%")
                    }

                    uiState.age?.let {
                        Text("Ay Yaşı: ${"%.1f".format(it)} gün")
                    }

                    uiState.distanceToMoon?.let {
                        Text("Dünya - Ay mesafesi: ${it} km")
                    }

                    uiState.distanceToSun?.let {
                        Text("Dünya - Güneş mesafesi: ${it / 1_000_000} milyon km")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(onClick = { onEvent(MoonOperationEvent.Moon) }) {
                        Text("Yenile")
                    }
                }
            }
        }
    }

}

@Composable
fun MoonPhaseVisual(illumination: Double, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(150.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2

        // Taban: karanlık daire
        drawCircle(
            color = Color.Black,
            radius = radius,
            center = center
        )

        // Faz oranı (0.0 = yeni ay, 1.0 = dolunay)
        val illum = illumination.coerceIn(0.0, 1.0).toFloat()

        // Gerçekçi ışık tarafı: eliptik parlaklık
        val leftOval = Rect(
            left = center.x - radius * illum,
            top = center.y - radius,
            right = center.x + radius * illum,
            bottom = center.y + radius
        )

        drawOval(
            color = Color.White,
            topLeft = leftOval.topLeft,
            size = leftOval.size
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun MoonOperationScreenPreview() {
    MoonOperationScreen(
        uiState = MoonOperationUiState(
            phaseName = "Waxing Gibbous",
            moonName = "Thunder Moon",
            illuminationPercent = 92,
            age = 12.01,
            distanceToMoon = 402095,
            distanceToSun = 152_094_789,
            error = null
        ),
        onEvent = {}
    )
}