package com.oyetech.composebase.sharedScreens.quotes.randomQuotesViewer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuotesUiState

/**
Created by Erdi Özbek
-16.12.2024-
-23:09-
 **/

// Main QuotesCard Composable
@Composable
fun RandomQuotesSmallView(uiState: QuotesUiState = QuotesUiState()) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Alıntı Text
            Text(
                text = uiState.text,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
            // Alıntı Text
            Text(
                text = uiState.createdAtString,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )

            // Sağ Alt Köşe: Yazar İsmi
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.BottomEnd
            ) {
                Text(
                    text = "- ${uiState.author}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun QuotesScreenPreview() {
    RandomQuotesSmallView(
        uiState = QuotesUiState(
            text = "Success is not final, failure is not fatal: It is the courage to continue that counts.",
            author = "Winston Churchill",
            htmlFormatted = "<i>Success is not final, failure is not fatal:</i> <b>It is the courage to continue that counts.</b>"
        )
    )
}
