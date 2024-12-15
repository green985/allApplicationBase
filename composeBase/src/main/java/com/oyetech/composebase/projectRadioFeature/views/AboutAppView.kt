package com.oyetech.composebase.projectRadioFeature.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectRadioFeature.RadioDimensions

@Composable
fun AboutAppView(
    appName: String,
    appVersion: String,
    isDebug: Boolean,
    logo: ImageBitmap?, // Pass the application logo as a Painter
) {
    Card(
        modifier = Modifier,
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Application Logo
                if (logo != null) {
                    Image(
                        bitmap = logo,
                        contentDescription = "Application Logo",
                        modifier = Modifier.size(RadioDimensions.appLogoSize)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Application Name
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Application Version
                Text(
                    text = "Version: $appVersion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Debug Mode Indicator
                if (isDebug) {
                    Text(
                        text = "Debug Mode",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewAboutAppView() {
    AboutAppView(
        appName = "Radio App",
        appVersion = "1.0.0",
        isDebug = true,
        logo = null
    )
}
