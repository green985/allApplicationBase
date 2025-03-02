package com.oyetech.composebase.sharedScreens.messaging.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.sharedScreens.messaging.MessageConversationUiState

/**
Created by Erdi Özbek
-17.02.2025-
-22:01-
 **/

@Composable
fun MessageConversationItemView(
    modifier: Modifier = Modifier,
    uiState: MessageConversationUiState,
) {

    Box(Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Person",
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "".plus(uiState.lastMessageUiSate?.messageText),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }

        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            text = uiState.createdAtString,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary
        )

        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
        )

    }
}

@Preview
@Composable
fun MessageConversationItemViewPreview() {
    MessageConversationItemView(
        uiState = MessageConversationUiState(
            username = "Erdi",
            conversationId = "1234567890"
        )
    )
}