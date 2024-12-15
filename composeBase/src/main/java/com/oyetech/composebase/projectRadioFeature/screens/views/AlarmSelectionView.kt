package com.oyetech.composebase.projectRadioFeature.screens.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-14.11.2024-
-15:05-
 **/

@Composable
fun AlarmSelectionView(
    modifier: Modifier = Modifier,
    isFirstInit: Boolean = false,
    isFirstInitClick: () -> Unit = {},
    isAlarmEnable: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {},
    alarmTimeString: String = "",
    alarmRadioName: String = "",
) {
    if (isFirstInit) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { isFirstInitClick.invoke() }
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Add Alarm",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
            )
        }
    } else {
        Row(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(text = alarmTimeString, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = alarmRadioName, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.weight(1f))
            Switch(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = isAlarmEnable,
                onCheckedChange = onCheckedChange
            )
        }
    }


}

@Preview(showSystemUi = true)
@Composable
fun AlarmSelectionViewPreview() {
    AlarmSelectionView(
        isFirstInit = true,
        isAlarmEnable = true,
        alarmTimeString = "12:00",
        alarmRadioName = "Radio Name"
    )
}

@Preview(showSystemUi = true)
@Composable
fun AlarmSelectionViewPreviewFirstInit() {
    AlarmSelectionView(
        isFirstInit = false,
        isAlarmEnable = false,
        alarmTimeString = "12:00",
        alarmRadioName = "Radio Name"
    )
}

@Preview(showSystemUi = true)
@Composable
fun AlarmSelectionViewPreviewFirstInit3() {
    AlarmSelectionView(
        isFirstInit = false,
        isAlarmEnable = true,
        alarmTimeString = "12:00",
        alarmRadioName = "Radio Name"
    )
}