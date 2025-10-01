package com.oyetech.composebase.sharedScreens.allScreenNavigator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffoldDeprecated
import com.oyetech.composebase.helpers.viewProperties.gridItems
import kotlinx.collections.immutable.toImmutableList
import org.koin.androidx.compose.koinViewModel

/**
Created by Erdi Özbek
-18.01.2025-
-11:09-
 **/

@Composable
fun AllScreenNavigatorScreenSetup(
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<AllScreenNavigatorVM>()
//
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val routeList = AllScreenNavigator.generalListOfScreen.toImmutableList()

    val isNavigate = remember { false }

    LaunchedEffect(isNavigate) {

    }

    BaseScaffoldDeprecated {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(it)
        ) {
            Spacer(modifier = Modifier.padding(16.dp))
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    viewModel.onEvent(AllScreenNavigatorEvent.OnNavigateToQuestionStart)
                }) {
                    Text(
                        text = "Question Application Start",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
//            Spacer(modifier = Modifier.padding(16.dp))
//            Row(
//                modifier = Modifier
//                    .padding(8.dp)
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Button(onClick = {
//                    viewModel.onEvent(AllScreenNavigatorEvent.OnNavigateToQuoteStart)
//                }) {
//                    Text(
//                        text = "Quote Application Start",
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                }
//            }

            LazyColumn(
                state = rememberLazyListState(),
            ) {
                gridItems(routeList, 2, itemContent = { model ->

                    Button(modifier = Modifier.padding(2.dp), onClick = {
                        viewModel.onEvent(AllScreenNavigatorEvent.NavigateListItemClicked(model))
                    }) {
                        Text(text = model)
                    }

                })
            }

        }
    }

}

@Composable
fun AllScreenNavigatorVMScreen(
    modifier: Modifier = Modifier,
    uiState: AllScreenNavigatorUiState,
    onEvent: (AllScreenNavigatorEvent) -> (Unit),
) {
    BaseScaffoldDeprecated {
        Column(modifier = Modifier.padding()) {

        }
    }

}