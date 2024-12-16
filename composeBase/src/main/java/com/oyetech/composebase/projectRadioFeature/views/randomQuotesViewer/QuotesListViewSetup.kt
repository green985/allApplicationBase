package com.oyetech.composebase.projectRadioFeature.views.randomQuotesViewer

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-16.12.2024-
-23:12-
 **/

@Composable
fun QuotesListViewSetup(vm: QuotesVM = koinViewModel()) {
    val complexItemListState by vm.complexItemListState.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        complexItemListState.items.firstOrNull()?.let {
            RandomQuotesSmallView(uiState = it)
        }

    }

}


