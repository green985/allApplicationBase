package com.oyetech.composebase.projectRadioFeature.screens.tabSettings.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-13.12.2024-
-12:52-
 **/

@Composable
fun SimpleSettingsInfoViewSetup(
    onClick: () -> Unit,
    text: String,
) {
    Column {

        Row(
            modifier = Modifier
                .clickable { onClick.invoke() }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        HorizontalDivider(
            modifier = Modifier.height(1.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTabSettingsScreenSetup() {
    SimpleSettingsInfoViewSetup(
        onClick = {},
        text = "Settings"
    )
}