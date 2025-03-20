package com.oyetech.composebase.sharedScreens.userList.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-20.03.2025-
-20:48-
 **/

@Suppress("FunctionName")
@Composable
fun UserListItemView(
    modifier: Modifier = Modifier,
    uiState: UserListItemUiState,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = uiState.username, style = MaterialTheme.typography.headlineMedium
            )
            Text(
                modifier = Modifier.align(Alignment.TopEnd),
                text = uiState.age,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                modifier = Modifier.align(Alignment.BottomEnd),
                text = uiState.lastTriggeredTimeString,
                style = MaterialTheme.typography.bodyMedium
            )

        }
        HorizontalDivider()
    }


}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun UserListItemViewPreview() {
    UserListItemView(
        uiState = UserListItemUiState(
            username = "Erdi", userId = "asdasd", age = "31", gender = "male",
            lastTriggeredTimeString = "tractatos",
        )
    )
}