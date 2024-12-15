package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
Created by Erdi Özbek
-7.12.2024-
-21:41-
 **/

enum class DeleteListOperationDialog {
    Delete_List_History,
    Delete_List_And_Favorite,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteListOperationDialogSetup(
    modifier: Modifier = Modifier,
    dialogType: String,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {
    val message = if (dialogType == DeleteListOperationDialog.Delete_List_History.name) {
        "Are you sure you want to delete the history list?"
    } else {
        "Are you sure you want to delete the favorite list?"
    }

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
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
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
        }

    )
}

@Preview
@Composable
private fun DeleteListOperationDialogSetup() {
    DeleteListOperationDialogSetup(
        dialogType = DeleteListOperationDialog.Delete_List_History.name,
        onDismiss = {},
        onSuccess = {}
    )
}
