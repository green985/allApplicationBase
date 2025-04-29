package com.oyetech.composebase.baseViews.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent
import com.oyetech.composebase.projectQuotesFeature.quotes.tagList.QuoteTagUiState

/**
Created by Erdi Özbek
-6.01.2025-
-22:26-
 **/

@Composable
fun <T> GenericPopupMenu(
    menuItems: List<T>,
    itemLabel: (T) -> String,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        content { expanded = true } // Tetikleyici bileşeni
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = itemLabel(item)
                        )
                    },
                    onClick = {
                        onItemClick(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Suppress("FunctionName")
@Composable
fun QuoteTagMenu(
    tagList: List<QuoteTagUiState>,
    selectedTagList: List<QuoteTagUiState>,
    onEvent: (AdviceQuoteEvent) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (onClick: () -> Unit) -> Unit,
) {
    var expanded by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)), content = { // Arka planı hafif karart) {

            if (expanded) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f)) // Arka planı hafif karart
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                expanded = false
                            }) // Dışarı tıklanınca kapat
                        }
                ) {

                    Box(
                        modifier = Modifier
//                    .align(Alignment.TopCenter)
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
//                            .padding(16.dp)
                            .shadow(8.dp)
                            .clickable { } // İçeriğe tıklayınca kapanmaması için
                    ) {

                    }
                }


            }
        })

//        content { expanded = true } // Tetikleyici bileşeni


}