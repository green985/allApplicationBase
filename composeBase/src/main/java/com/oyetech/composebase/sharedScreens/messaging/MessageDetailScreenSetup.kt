package com.oyetech.composebase.sharedScreens.messaging

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-16.02.2025-
-19:00-
 **/

@Suppress("FunctionName")
@Composable
fun MessageDetailScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<MessageDetailVm>()

    val uiState by vm.uiState.collectAsStateWithLifecycle()

    MessageDetailScreen(
        messageText = uiState.messageText,
        onMessageTextChanged = { vm.onEvent(MessageDetailEvent.OnMessageTextChange(it)) },
        onMessageSend = { vm.onEvent(MessageDetailEvent.OnMessageSend) }
    )


}

@Suppress("FunctionName")
@Composable
fun MessageDetailScreen(
    modifier: Modifier = Modifier,
    onMessageTextChanged: (String) -> Unit,
    onMessageSend: () -> Unit,
    messageText: String,
) {

    BaseScaffold {
        Column(modifier = Modifier.padding(it)) {
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp)
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = messageText,
                    onValueChange = { onMessageTextChanged(it) },
                    shape = RoundedCornerShape(RadioDimensions.inputFieldRadius),
                    label = { Text(LanguageKey.commentInputAreaHint) },
                    trailingIcon = {
                        IconButton(enabled = messageText.isNotBlank(), onClick = {
                            onMessageSend()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Localized description",
                            )
                        }
                    }
                )
            }
            Spacer(Modifier.size(16.dp))

        }
    }


}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MessageDetailPreview() {
    MessageDetailScreen(messageText = "Hello", onMessageSend = {}, onMessageTextChanged = {}

    )

}