package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.oyetech.composebase.R
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.StartBehaviourDialogSelection.All_Stations
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.StartBehaviourDialogSelection.Favorite_List
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.StartBehaviourDialogSelection.Last_Listened

/**
Created by Erdi Özbek
-14.11.2024-
-16:06-
 **/

enum class StartBehaviourDialogSelection {
    All_Stations,
    Favorite_List,
    Last_Listened


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartBehaviourSelectionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onSuccess: () -> Unit = {},
    selectedSetting: StartBehaviourDialogSelection = StartBehaviourDialogSelection.All_Stations,
    onSettingSelected: (StartBehaviourDialogSelection) -> Unit = {},
) {

    BasicAlertDialog(onDismiss,
        modifier = modifier
            .padding(16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        content = {

            Column(modifier = Modifier) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {

                    Text(
                        text = "Start Behaviour Selection",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                StartBeheviourSelectionView(
                    stringResource(R.string.nav_item_stations),
                    onSettingSelected,
                    selectedSetting == All_Stations
                )

                StartBeheviourSelectionView(
                    stringResource(R.string.nav_item_starred),
                    onSettingSelected,
                    selectedSetting == Favorite_List
                )

                StartBeheviourSelectionView(
                    stringResource(R.string.nav_item_history),
                    onSettingSelected,
                    selectedSetting == Last_Listened
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = onDismiss
                    ) {
                        Text(text = "Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), onClick = onSuccess
                    ) {
                        Text(text = "Ok")
                    }

                }

            }

        })
}

@Composable
private fun StartBeheviourSelectionView(
    text: String,
    onSettingSelected: (StartBehaviourDialogSelection) -> Unit,
    isSelected: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSettingSelected(All_Stations)
            }
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { },
            enabled = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Preview(showBackground = true)
@Composable
fun StartBehaviourSelectionDialogPreview() {
    StartBehaviourSelectionDialog()
}