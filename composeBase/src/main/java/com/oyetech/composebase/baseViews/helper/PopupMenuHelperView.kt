package com.oyetech.composebase.baseViews.helper

import androidx.compose.foundation.layout.Box
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