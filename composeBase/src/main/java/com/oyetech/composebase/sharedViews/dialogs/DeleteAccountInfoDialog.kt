package com.oyetech.composebase.sharedViews.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import com.oyetech.languageModule.keyset.LanguageKey

@Composable
fun DeleteAccountInfoDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = LanguageKey.deleteAccountButtonText,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = LanguageKey.deleteDialogInfo,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = LanguageKey.delete)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = LanguageKey.cancel)
            }
        }
    )
}
