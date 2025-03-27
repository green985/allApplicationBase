package com.oyetech.composebase.projectQuotesFeature.views.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-27.03.2025-
-21:46-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDialogOperation(
    onDismiss: () -> Unit = {},
    titleText: String,
    descriptionText: String,
) {

    BasicAlertDialog(
        onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        content = {

            Surface(
                modifier = Modifier
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = (titleText),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                    Text(
                        text = (descriptionText),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(LanguageKey.dismiss)
                    }
                }

            }
        })
}

@Preview(showBackground = true)
@Composable
private fun InfoDialogOperationPreview() {
    InfoDialogOperation(
        titleText = "Title",
        descriptionText = "Description"
    )
}
