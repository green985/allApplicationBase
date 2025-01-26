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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-26.01.2025-
-18:48-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountInfoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    BasicAlertDialog(
        onDismiss,
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
                    text = LanguageKey.deleteDialogInfo,
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
                            .weight(1f), onClick = onConfirm
                    ) {
                        Text(text = "Ok")
                    }

                }
            }
        })
}
