package com.oyetech.composebase.sharedScreens.quotes.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.experimental.commentWidget.CommentScreenWithContentScreenSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.OnActionButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.sharedScreens.quotes.detail.QuoteDetailEvent.ClickNextButton
import com.oyetech.composebase.sharedScreens.quotes.detail.QuoteDetailEvent.ClickPreviousButton
import com.oyetech.composebase.sharedScreens.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.sharedScreens.quotes.uiState.QuoteUiState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

/**
Created by Erdi Özbek
-17.01.2025-
-21:28-
 **/

@Suppress("FunctionNaming")
@Composable
fun QuoteDetailScreenSetup(
    modifier: Modifier = Modifier,
    quoteId: String,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    val vm = koinViewModel<QuoteDetailVm>(key = quoteId) {
        parametersOf(
            quoteId
        )
    }

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarState by vm.toolbarState.collectAsStateWithLifecycle()

    QuoteDetailScreen(
        modifier = modifier,
        uiState = uiState,
        toolbarState = toolbarState,
        onToolbarEvent = {
            when (it) {
                is BackButtonClick -> {
                    navigationRoute.invoke("back")
                }

                is OnActionButtonClick -> TODO()
            }
            vm.onToolbarEvent(it)
        }, onEvent = {
            vm.onEvent(it)
        }, navigationRoute
    )
}

@Composable
fun QuoteDetailScreen(
    modifier: Modifier = Modifier,
    uiState: QuoteUiState,
    toolbarState: QuoteToolbarState,
    onToolbarEvent: (QuoteToolbarEvent) -> Unit,
    onEvent: (QuoteDetailEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit,
) {

    BaseScaffold {
        Column {

            Column(
                modifier = Modifier
                    .padding()
                    .weight(1f)
            ) {
                QuoteToolbarSetup(uiState = toolbarState, onEvent = {
                    onToolbarEvent(it)
                }
                )
                RandomQuotesSmallView(uiState = uiState, navigationRoute = navigationRoute)
                CommentScreenWithContentScreenSetup(uiState.quoteId)

            }
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = { onEvent(ClickNextButton) }) {
                    Icon(
                        modifier = Modifier
                            .rotate(180f)
                            .size(RadioDimensions.radioLogoSmallWidthHeight),
                        painter = painterResource(R.drawable.ic_skip_next_circle),
                        contentDescription = "Next"
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onEvent(ClickPreviousButton) }) {
                    Icon(
                        modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
                        painter = painterResource(R.drawable.ic_skip_next_circle),
                        contentDescription = "Previous"
                    )
                }

            }
        }

    }


}

sealed class QuoteDetailEvent {
    object ClickNextButton : QuoteDetailEvent()
    object ClickPreviousButton : QuoteDetailEvent()
}
