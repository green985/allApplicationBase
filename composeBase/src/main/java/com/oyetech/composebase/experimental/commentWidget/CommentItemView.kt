package com.oyetech.composebase.experimental.commentWidget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.baseViews.helper.GenericPopupMenu
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.DeleteComment
import com.oyetech.composebase.experimental.commentWidget.CommentOptionsEvent.ReportComment
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.languageModule.keyset.LanguageKey

@Composable
fun CommentItemView(uiState: CommentItemUiState, onEvent: (CommentScreenEvent) -> (Unit)) {

    val cardContainerColor = if (uiState.isMine) {
        AppColors.primaryMuted
    } else {
        AppColors.surfaceVariant
    }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (uiState.isMine) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {

        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(0.7f),
            colors = CardDefaults.cardColors(
                containerColor = cardContainerColor,
            )
        ) {
            Column(Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        modifier = Modifier.weight(1f),
                        text = uiState.commentContent,
                        style = AppTextStyles.body,
                    )
                    GenericPopupMenu(
                        menuItems = listOf(LanguageKey.delete),
                        itemLabel = { it },
                        onItemClick = {
                            when (it) {
                                "Delete" -> {
                                    onEvent(DeleteComment(uiState.commentId))
                                }

                                "Report" -> {
                                    onEvent(ReportComment(uiState.commentId))
                                }
                            }
                        },
                        content = {
                        }
                    )

                    GenericPopupMenu(
                        menuItems = listOf(LanguageKey.delete),
                        itemLabel = { it },
                        onItemClick = {
                            when (it) {
                                LanguageKey.delete -> {
                                    onEvent(DeleteComment(uiState.commentId))
                                }

                                "Report" -> {
                                    onEvent(ReportComment(uiState.commentId))
                                }
                            }
                        },
                    ) {
                        Icon( // todo will be changed
                            modifier = Modifier.clickable {
                                it.invoke()
                            },
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null,
                            tint = AppColors.primary,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon( // todo will be changed
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = AppColors.primary,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.username,
                            color = AppColors.primary,
                            style = AppTextStyles.titleSmall,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = uiState.createdAtString,
                        style = AppTextStyles.bodySecondary,
                        color = AppColors.textSecondary,
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun CommentItemViewPreview() {

    CommentItemView(
        uiState = CommentItemUiState(
            commentId = "1",
            commentContent = "This is a comment",
            createdAtString = "31.12.2024 00:00",
            username = "Erdi",
            isMine = true
        )
    ) {}
}
