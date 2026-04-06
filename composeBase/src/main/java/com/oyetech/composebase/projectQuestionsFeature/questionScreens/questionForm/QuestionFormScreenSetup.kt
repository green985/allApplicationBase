package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListWithParamsContent
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.AppShapes
import com.oyetech.composebase.projectQuestionsFeature.theme.AppSpacing
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Created by Erdi Özbek
 * -17.11.2025-
 *
 * Main screen setup with ViewModel integration
 */
@Composable
fun QuestionFormScreenSetup(
    modifier: Modifier = Modifier,
    formId: String = "",
) {
    val viewModel = koinViewModel<QuestionFormViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listUiState2 by viewModel.listUiState2.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formId) {
        // TODO: Load form data from repository
        viewModel.onEvent(QuestionFormEvent.OnFormLoad("form1"))
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is QuestionFormUiEvent.OnSubmitSuccess -> {
                    snackbarHostState.showSnackbar("Form submitted successfully!")
                }

                is QuestionFormUiEvent.OnSubmitError -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is QuestionFormUiEvent.OnFormLocked -> {
                    snackbarHostState.showSnackbar("Form locked")
                }

                is QuestionFormUiEvent.OnFormUnlocked -> {
                    snackbarHostState.showSnackbar("Form unlocked for editing")
                }

                is QuestionFormUiEvent.OnNavigateBack -> {
                }
            }
        }
    }

    QuestionFormScreen(
        uiState = uiState,
        listUiState = listUiState2,
        onQuestionEvent = { viewModel.onQuestionEvent(it) },
        snackbarHostState = snackbarHostState,
        onEvent = { viewModel.onEvent(it) }
    )
}

/**
 * Main screen composable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionFormScreen(
    uiState: QuestionFormScreenUiState,
    listUiState: GenericListState<QuestionViewUiState> = GenericListState.empty(),
    onQuestionEvent: (QuestionViewEvent) -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (QuestionFormEvent) -> Unit = {},
) {
    BaseScaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Question Form",
                        style = AppTextStyles.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(QuestionFormEvent.OnBackPressed) }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreenFullSize()
            uiState.isError -> ErrorScreenFullSize(
                errorText = uiState.errorText,
                onRetry = { onEvent(QuestionFormEvent.OnErrorDismiss) },
            )

            else -> QuestionFormContent(
                modifier = Modifier.padding(paddingValues),
                uiState = uiState,
                listUiState = listUiState,
                onQuestionEvent = onQuestionEvent,
                onEvent = onEvent,
            )
        }
    }
}

/**
 * Main content area
 */
@Composable
private fun QuestionFormContent(
    modifier: Modifier = Modifier,
    uiState: QuestionFormScreenUiState,
    listUiState: GenericListState<QuestionViewUiState>,
    onQuestionEvent: (QuestionViewEvent) -> Unit = {},
    onEvent: (QuestionFormEvent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
        contentPadding = PaddingValues(bottom = AppSpacing.xxxl),
    ) {
        item {
            FormTitleSection(
                title = uiState.title,
                description = uiState.description,
                isLocked = uiState.isLocked,
                submittedAt = uiState.submittedAt,
                isGeneratingResult = uiState.isGeneratingResult,
                generatedResultText = uiState.generatedResultText,
            )
        }

        if (!uiState.isLocked) {
            item {
                ProgressSection(
                    answeredCount = uiState.answeredCount,
                    totalCount = uiState.totalCount,
                )
            }
        }

        item {
            Text(
                text = "Questions",
                style = AppTextStyles.titleSmall,
                modifier = Modifier.padding(horizontal = AppSpacing.xl, vertical = AppSpacing.xs),
            )
        }

        items(1) {
            QuestionListWithParamsContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentPadding = PaddingValues(horizontal = 0.dp),
                listViewState = listUiState,
                onQuestionEvent = onQuestionEvent,
            )
        }

        item {
            FormActionButtons(
                canSubmit = uiState.canSubmit,
                isLocked = uiState.isLocked,
                onSubmit = { onEvent(QuestionFormEvent.OnSubmitForm) },
            )
        }
    }
}

/**
 * Form title and description section
 */
@Composable
@Suppress("LongMethod", "LongParameterList")
private fun FormTitleSection(
    title: String,
    description: String,
    isLocked: Boolean,
    submittedAt: Long?,
    isGeneratingResult: Boolean = false,
    generatedResultText: String = "",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.xl)
            .padding(top = AppSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = AppShapes.roundedLarge,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppSpacing.xl),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
            ) {
                if (isLocked && submittedAt != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(AppSpacing.xl)
                                .clip(AppShapes.roundedPill)
                                .background(AppColors.primary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Submitted",
                                tint = AppColors.onPrimary,
                                modifier = Modifier.size(AppSpacing.md),
                            )
                        }
                        Text(
                            text = "Submitted",
                            style = AppTextStyles.label,
                            color = AppColors.primary,
                        )
                    }
                }

                Text(
                    text = title,
                    style = AppTextStyles.titleLarge,
                    color = AppColors.textPrimary,
                )

                if (description.isNotBlank()) {
                    Text(
                        text = description,
                        style = AppTextStyles.body,
                        color = AppColors.textSecondary,
                    )
                }
            }
        }

        if (isGeneratingResult || generatedResultText.isNotBlank()) {
            Card(
                modifier = modifier.fillMaxWidth(),
                shape = AppShapes.roundedLarge,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                ) {
                    Text(
                        text = "AI Değerlendirme",
                        style = AppTextStyles.titleSmall,
                        color = AppColors.primary,
                    )

                    if (isGeneratingResult) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(AppSpacing.md),
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(AppSpacing.xxxl),
                                    strokeWidth = AppSpacing.xxs,
                                )
                                Text(
                                    text = "Yanıtlarınız analiz ediliyor...",
                                    style = AppTextStyles.bodySecondary,
                                    color = AppColors.textSecondary,
                                )
                            }
                        }
                    } else {
                        Text(
                            text = generatedResultText,
                            style = AppTextStyles.body,
                            color = AppColors.textPrimary,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Progress indicator section
 */
@Composable
private fun ProgressSection(
    answeredCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalCount > 0) answeredCount.toFloat() / totalCount else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Progress",
                style = AppTextStyles.label,
                color = AppColors.textSecondary,
            )
            Text(
                text = "$answeredCount / $totalCount",
                style = AppTextStyles.label,
                color = if (progress >= 1f) AppColors.primary else AppColors.textSecondary,
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(AppShapes.roundedPill),
        )
    }
}

/**
 * Action buttons section
 */
@Composable
private fun FormActionButtons(
    canSubmit: Boolean,
    isLocked: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = !isLocked,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.xl),
    ) {
        Button(
            onClick = onSubmit,
            enabled = canSubmit,
            shape = AppShapes.roundedLarge,
            contentPadding = PaddingValues(vertical = AppSpacing.lg),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Submit Form",
                style = AppTextStyles.button,
            )
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun PreviewQuestionFormScreen() {
    QuestionFormScreen(
        uiState = previewQuestionFormScreenUiState(isSubmitted = false, questionsCount = 3),
        listUiState = GenericListState<QuestionViewUiState>()
            .copy(items = previewQuestionFormScreenUiState(questionsCount = 3).questions),
        onQuestionEvent = TODO(),
        snackbarHostState = TODO(),
        onEvent = TODO(),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionFormScreenSubmitted() {
    QuestionFormScreen(
        uiState = previewQuestionFormScreenUiState(isSubmitted = true, questionsCount = 3)
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionFormScreenLoading() {
    QuestionFormScreen(
        uiState = previewLoadingState()
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionFormScreenError() {
    QuestionFormScreen(
        uiState = previewErrorState()
    )
}
