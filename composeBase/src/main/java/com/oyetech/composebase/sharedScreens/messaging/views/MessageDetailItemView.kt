import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailUiState
import com.oyetech.models.firebaseModels.messagingModels.MessageStatus

@Composable
fun MessageDetailItemView(
    uiState: MessageDetailUiState,
    currentUserId: String, // Şu anki kullanıcının ID'si
    modifier: Modifier = Modifier,
) {
    val isCurrentUser = uiState.senderId == currentUserId

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = if (isCurrentUser) 16.dp else 0.dp,
                topEnd = if (isCurrentUser) 0.dp else 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier
                .widthIn(min = 60.dp, max = 280.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
            ) {
                Text(
                    text = uiState.content,
                    fontSize = 16.sp,
                    color = if (isCurrentUser) Color.White else Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = uiState.createdAtString,
                        fontSize = 12.sp,
                        color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    MessageStatusIcon(uiState.status)
                }
            }
        }
    }
}

@Composable
private fun MessageStatusIcon(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.IDLE -> ""
        MessageStatus.SENT -> "✓"
//        MessageStatus.RECEIVED -> "✓✓"
//        MessageStatus.READ -> "✓✓"
        else -> ""
    }

    Text(
        text = icon,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = when (status) {
            MessageStatus.READ -> Color.Blue
            else -> Color.Gray
        }
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun MessageDetailItemPreview() {
    Column {

        MessageDetailItemView(
            uiState = MessageDetailUiState(
                content = "Hello, how are you?",
                createdAtString = "12:34",
                senderId = "senderId",
                receiverId = "receiverId",
                conversationId = "conversationId"
            ),
            currentUserId = "senderId",
        )
        MessageDetailItemView(
            uiState = MessageDetailUiState(
                content = "Hello, how are you?",
                createdAtString = "12:34",
                senderId = "senderId",
                receiverId = "receiverId",
                conversationId = "conversationId"
            ),
            currentUserId = "senderId2",
        )
    }
}
