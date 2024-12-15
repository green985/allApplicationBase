package com.oyetech.composebase.projectRadioFeature.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-14.11.2024-
-15:45-
 **/

@Composable
fun SettingsSimpleNavigationView(
    modifier: Modifier = Modifier,
    settingsTitleName: String,
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = settingsTitleName,
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = "Settings",
            modifier = Modifier.clickable { onClick.invoke() }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SettingsSimpleNavigationViewPreview() {
    SettingsSimpleNavigationView(
        settingsTitleName = "Settings",
        onClick = {}
    )
}