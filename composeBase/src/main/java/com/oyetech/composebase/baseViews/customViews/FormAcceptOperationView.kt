package com.oyetech.composebase.baseViews.customViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-21.04.2025-
-14:28-
 **/

@Composable
@Suppress("FunctionNaming")
fun FormAcceptOperationViewSetup(
    modifier: Modifier = Modifier,
    onCancelOperation: (() -> Unit),
    onAcceptOperation: (() -> Unit),
) {

    Column {

        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onCancelOperation) {
                Text(text = LanguageKey.cancel)
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = onAcceptOperation) {
                Text(text = LanguageKey.save)
            }
        }
        HorizontalDivider()
    }

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
@Suppress("FunctionNaming")
private fun FormAcceptOperationViewPreview() {
    FormAcceptOperationViewSetup(onCancelOperation = {}, onAcceptOperation = {})

}