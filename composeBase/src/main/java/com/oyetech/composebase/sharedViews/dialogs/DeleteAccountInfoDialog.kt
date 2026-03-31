package com.oyetech.composebase.sharedViews.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles

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
                style = AppTextStyles.titleLarge,
            )
        },
        text = {
            Text(
                text = LanguageKey.deleteDialogInfo,
                style = AppTextStyles.body,
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
