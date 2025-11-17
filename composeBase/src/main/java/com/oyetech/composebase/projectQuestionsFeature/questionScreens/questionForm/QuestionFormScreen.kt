package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewScaffoldLayout
import kotlinx.collections.immutable.ImmutableList
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
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(formId) {
        // TODO: Load form data from repository
        // viewModel.loadForm(formId)
        // For now, initialize with sample data
        viewModel.initializeForm(
            formId = formId,
            title = "Sample Form Title",
            description = "This is a sample form description.",
            questions = emptyList()
        )
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
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onEvent: (QuestionFormEvent) -> Unit = {},
) {
    BaseScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Question Form") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(QuestionFormEvent.OnBackPressed) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreenFullSize()
            }

            uiState.isError -> {
                ErrorScreenFullSize(
                    errorMessage = uiState.errorText,
                    onRetry = { onEvent(QuestionFormEvent.OnErrorDismiss) }
                )
            }

            else -> {
                QuestionFormContent(
                    uiState = uiState,
                    onEvent = onEvent,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * Main content area
 */
@Composable
private fun QuestionFormContent(
    uiState: QuestionFormScreenUiState,
    onEvent: (QuestionFormEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Section
        item {
            FormTitleSection(
                title = uiState.title,
                description = uiState.description,
                isLocked = uiState.isLocked,
                submittedAt = uiState.submittedAt
            )
        }

        // Progress Indicator
        if (!uiState.isLocked) {
            item {
                ProgressSection(
                    answeredCount = uiState.getAnsweredCount(),
                    totalCount = uiState.getTotalQuestions()
                )
            }
        }

        // Questions Catalog
        item {
            Text(
                text = "Questions",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(uiState.questions, key = { it.questionId }) { questionItem ->
            QuestionViewScaffoldLayout(
                uiState = questionItem, onEvent = {}, modifier =

            )



            QuestionItemCard(
                questionItem = questionItem,
                isLocked = uiState.isLocked,
                onAnswerSelected = { optionId ->
                    onEvent(QuestionFormEvent.OnQuestionAnswered(questionItem.questionId, optionId))
                },
                onClearAnswer = {
                    onEvent(QuestionFormEvent.OnClearAnswer(questionItem.questionId))
                },
                onExpandToggle = { isExpanded ->
                    onEvent(
                        QuestionFormEvent.OnQuestionExpanded(
                            questionItem.questionId,
                            isExpanded
                        )
                    )
                }
            )
        }

        // Submit/Edit Buttons
        item {
            FormActionButtons(
                canSubmit = uiState.canSubmit,
                isLocked = uiState.isLocked,
                onSubmit = { onEvent(QuestionFormEvent.OnSubmitForm) },
                onEdit = { onEvent(QuestionFormEvent.OnEditForm) },
                onCancelEdit = { onEvent(QuestionFormEvent.OnCancelEdit) }
            )
        }

        // Comments Section (visible after submit)
        if (uiState.isSubmitted) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                CommentsSection(
                    comments = uiState.comments,
                    commentInputText = uiState.commentInputText,
                    onCommentTextChanged = { text ->
                        onEvent(QuestionFormEvent.OnCommentTextChanged(text))
                    },
                    onAddComment = { onEvent(QuestionFormEvent.OnAddComment) },
                    onDeleteComment = { commentId ->
                        onEvent(QuestionFormEvent.OnDeleteComment(commentId))
                    }
                )
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * Form title and description section
 */
@Composable
private fun FormTitleSection(
    title: String,
    description: String,
    isLocked: Boolean,
    submittedAt: Long?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            if (isLocked && submittedAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Submitted",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Submitted",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$answeredCount / $totalCount",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = if (totalCount > 0) answeredCount.toFloat() / totalCount else 0f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Individual question card
 */
@Composable
private fun QuestionItemCard(
    questionItem: QuestionItemState,
    isLocked: Boolean,
    onAnswerSelected: (String) -> Unit,
    onClearAnswer: () -> Unit,
    onExpandToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Question Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = questionItem.questionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                if (questionItem.isAnswered) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Answered",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Options (placeholder - will be replaced with actual question view)
            AnimatedVisibility(visible = questionItem.isExpanded) {
                Column {
                    Text(
                        text = "Question options will be rendered here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (questionItem.selectedOptionId != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Selected: ${questionItem.selectedOptionId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (!isLocked && questionItem.isAnswered) {
                        TextButton(onClick = onClearAnswer) {
                            Text("Clear Answer")
                        }
                    }
                }
            }
        }
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
    onEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnimatedVisibility(
            visible = !isLocked,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Button(
                onClick = onSubmit,
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Form")
            }
        }

        AnimatedVisibility(
            visible = isLocked,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Edit Form")
                }
            }
        }
    }
}

/**
 * Comments section
 */
@Composable
private fun CommentsSection(
    comments: ImmutableList<FormComment>,
    commentInputText: String,
    onCommentTextChanged: (String) -> Unit,
    onAddComment: () -> Unit,
    onDeleteComment: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Comment input
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = commentInputText,
                    onValueChange = onCommentTextChanged,
                    placeholder = { Text("Add a comment...") },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onAddComment,
                    enabled = commentInputText.isNotBlank()
                ) {
                    Text("Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comments list
            if (comments.isEmpty()) {
                Text(
                    text = "No comments yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                comments.forEach { comment ->
                    CommentItem(
                        comment = comment,
                        onDelete = { onDeleteComment(comment.commentId) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

/**
 * Individual comment item
 */
@Composable
private fun CommentItem(
    comment: FormComment,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.commentText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete comment",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun PreviewQuestionFormScreen() {
    QuestionFormScreen(
        uiState = previewQuestionFormScreenUiState(isSubmitted = false, questionsCount = 3)
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

