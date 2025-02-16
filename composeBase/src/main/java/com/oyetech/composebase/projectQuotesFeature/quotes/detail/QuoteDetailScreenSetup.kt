package com.oyetech.composebase.projectQuotesFeature.quotes.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.R
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.experimental.commentWidget.CommentScreenWithContentScreenSetup
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuotesFeature.QuotesDimensions
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationEvent
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationUiState
import com.oyetech.composebase.projectQuotesFeature.contentOperation.ContentOperationVm
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.ClickNextButton
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.ClickPreviousButton
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailEvent.LongClickForCopy
import com.oyetech.composebase.projectQuotesFeature.quotes.randomQuotesViewer.RandomQuotesSmallView
import com.oyetech.composebase.projectQuotesFeature.quotes.uiState.QuoteUiState
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.BackButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarEvent.OnActionButtonClick
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarSetup
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
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
    vm: QuoteDetailVm = koinViewModel<QuoteDetailVm> {
        parametersOf(
            quoteId
        )
    },
    contentOperationVm: ContentOperationVm = koinViewModel<ContentOperationVm>(),
) {

    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val toolbarState by vm.toolbarState.collectAsStateWithLifecycle()
    val contentOperationState by contentOperationVm.contentOperationUiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.quoteId) {
        contentOperationVm.initContentOperationState(quoteId)
    }

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
        },
        onEvent = {
            vm.onEvent(it)
        },
        navigationRoute,
        contentOperationUiState = contentOperationState,
        contentOperationEvent = { contentOperationVm.onContentEvent(it) },
        contentOperationActive = true,
    )

    if (uiState.isLoading) {
        LoadingScreenFullSize()
    }
}

@Suppress("FunctionNaming", "LongParameterList")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteDetailScreen(
    modifier: Modifier = Modifier,
    uiState: QuoteUiState,
    toolbarState: QuoteToolbarState,
    onToolbarEvent: (QuoteToolbarEvent) -> Unit,
    onEvent: (QuoteDetailEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit,
    contentOperationUiState: ContentOperationUiState,
    contentOperationEvent: (ContentOperationEvent) -> Unit,
    contentOperationActive: Boolean,
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
                RandomQuotesSmallView(
                    modifier = Modifier.combinedClickable(
                        onClick = {

                        },
                        // Normal tıklama olayını boş bırakabilirsin
                        onLongClick = {
                            onEvent(LongClickForCopy)
                        }
                    ), uiState = uiState, navigationRoute = navigationRoute,
                    contentOperationUiState = contentOperationUiState,
                    contentOperationEvent = contentOperationEvent,
                    contentOperationActive = contentOperationActive

                )

                if (GeneralSettings.isCommentSectionEnable()) {
                    CommentScreenWithContentScreenSetup(uiState.quoteId)
                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    modifier = Modifier.size(QuotesDimensions.buttonHeight),
                    onClick = { onEvent(ClickNextButton) }) {
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
                    modifier = Modifier.size(QuotesDimensions.buttonHeight),
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
    object LongClickForCopy : QuoteDetailEvent()
}
