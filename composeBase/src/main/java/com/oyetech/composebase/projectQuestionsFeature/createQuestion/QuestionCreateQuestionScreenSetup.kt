package com.oyetech.composebase.projectQuestionsFeature.createQuestion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.languageModule.keyset.LanguageKey
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionCreateQuestionScreenSetup(
    modifier: Modifier = Modifier,
) {
    val vm = koinViewModel<QuestionCreateQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    QuestionCreateQuestionScreen(
        uiState = uiState,
        onEvent = { event: QuestionCreateQuestionEvent -> vm.onEvent(event) },
    )

    LaunchedEffect(Unit) {
        vm.uiEvent.collectLatest { event ->
            when (event) {
                is QuestionCreateQuestionUiEvent.OnSubmitSuccess -> {
                    // Navigation is handled inside ViewModel via NavigationUseCase
                }

                is QuestionCreateQuestionUiEvent.OnSubmitError -> {
                    // TODO: handle error UI feedback
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
private fun QuestionCreateQuestionToolbar(title: String) {
    TopAppBar(title = {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuestionCreateQuestionContent(
    uiState: QuestionCreateQuestionScreenUiState,
    onEvent: (QuestionCreateQuestionEvent) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.titleText,
            onValueChange = { onEvent(QuestionCreateQuestionEvent.OnTitleChange(it)) },
            label = { Text(LanguageKey.appName) }
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = uiState.descriptionText,
            onValueChange = { onEvent(QuestionCreateQuestionEvent.OnDescriptionChange(it)) },
            label = { Text(LanguageKey.commentInputAreaHint) }
        )
        Button(
            enabled = uiState.isSubmitEnabled && !uiState.isLoading,
            onClick = { onEvent(QuestionCreateQuestionEvent.OnSubmit) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(LanguageKey.save)
        }
    }
}

@Composable
private fun QuestionCreateQuestionScreen(
    uiState: QuestionCreateQuestionScreenUiState,
    onEvent: (QuestionCreateQuestionEvent) -> Unit,
) {
    BaseScaffold(
        topBar = { QuestionCreateQuestionToolbar(uiState.toolbarTitleText) },
        content = { innerPadding ->
            // Apply only top padding from BaseScaffold
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            ) {
                QuestionCreateQuestionContent(
                    uiState = uiState,
                    onEvent = onEvent,
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun QuestionCreateQuestionPreview() {
    QuestionCreateQuestionScreen(
        uiState = QuestionCreateQuestionScreenUiState(
            titleText = "Sample",
            descriptionText = "Body"
        ),
        onEvent = {},
    )
}
