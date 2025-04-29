package com.oyetech.composebase.projectQuotesFeature.views.toolbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oyetech.composebase.R
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-30.11.2024-
-18:39-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteToolbarSetup(
    uiState: QuoteToolbarState,
    onEvent: (QuoteToolbarEvent) -> Unit = {},
) {
    val isCenterText = false

    Column {
        TopAppBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = if (isCenterText) TextAlign.Center else null
                )
            },
            navigationIcon = {
                if (uiState.showBackButton) {
                    IconButton(onClick = {
                        onEvent(
                            QuoteToolbarEvent.BackButtonClick(
                                handleWithNavigation = true
                            )
                        )
                    }) {
                        Icon(Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                uiState.actionButtonState.forEach { button ->
                    BadgedBox(
                        badge = {
                        }
                    ) {
                        IconButton(
                            onClick = { onEvent(QuoteToolbarEvent.OnActionButtonClick(button)) }) {
                            Icon(
                                painter = painterResource(button.getDrawableResource()),
                                contentDescription = button.javaClass.name
                            )
                        }
                    }

                }
            }
        )
        HorizontalDivider(
            modifier = Modifier.height(1.dp)
        )
    }
}

@Preview
@Composable
private fun QuoteToolbarPreview() {
    QuoteToolbarSetup(
        QuoteToolbarState(
            title = "Quote Everyone",
            showBackButton = false,
            actionButtonState = persistentListOf(
                QuoteToolbarActionItems.Timer(R.drawable.ic_timer),
            )
        )
    )

}