package com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.views.questions.CreateQuestionYesNoView
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionCreateScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuestionCreateQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val questionUiState by vm.questionUiState.collectAsStateWithLifecycle()

    QuestionCreateScreen(
        uiState = uiState,
        questionUiState = questionUiState,
        onEvent = { event: QuestionViewEvent -> vm.onEvent(event) },
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                is QuestionCreateQuestionUiEvent.OnSubmitSuccess -> {
                    // Navigation is handled inside ViewModel via NavigationUseCase
                }

                is QuestionCreateQuestionUiEvent.OnSubmitError -> {

                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.onEvent(QuestionCreateQuestionEvent.OnScreenOut)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionCreateToolbar(title: String) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    })
}

@Composable
private fun QuestionCreateScreen(
    uiState: QuestionCreateQuestionScreenUiState,
    onEvent: (QuestionViewEvent) -> Unit = {},
    questionUiState: QuestionViewUiState,
) {
    BaseScaffold(
        topBar = { QuestionCreateToolbar(uiState.toolbarTitleText) },
        content = { innerPadding ->
            // Apply only top padding from BaseScaffold
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                if (uiState.isLoading) {
                    LoadingScreenFullSize()
                }
                if (uiState.errorText.isNotBlank()) {
                    ErrorScreenFullSize(
                        errorMessage = uiState.errorText,
                        onDismiss = { onEvent(QuestionViewEvent.OnErrorDismiss) }
                    )
                }
                CreateQuestionYesNoView(
                    uiState = questionUiState, onEvent = onEvent
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun QuestionCreatePreview() {
    QuestionCreateScreen(
        uiState = QuestionCreateQuestionScreenUiState(
            titleText = "Sample",
            descriptionText = "Body"
        ),
        onEvent = {}, questionUiState = QuestionViewUiState(titleText = "asdasdasd"),
    )
}
