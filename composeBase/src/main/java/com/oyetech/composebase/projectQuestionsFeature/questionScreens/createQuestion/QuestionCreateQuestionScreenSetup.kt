package com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.baseViews.loadingErrors.ErrorScreenFullSize
import com.oyetech.composebase.baseViews.loadingErrors.LoadingScreenFullSize
import com.oyetech.composebase.helpers.general.GeneralSettings
import com.oyetech.composebase.projectQuestionsFeature.views.questions.CreateQuestionYesNoView
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionTagsAreaContainer
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewEvent
import com.oyetech.composebase.projectQuestionsFeature.views.questions.QuestionViewUiState
import com.oyetech.models.questionProject.questionOperation.QuestionCategories
import com.oyetech.models.questionProject.questionOperation.QuestionCategoryKeys
import com.oyetech.models.questionProject.questionOperation.ThreeChoiceSubCategories
import com.oyetech.models.questionProject.questionOperation.TwoChoiceSubCategories
import com.oyetech.models.questionProject.questionOperation.asKey
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun QuestionCreateScreenSetup(
    modifier: Modifier = Modifier,
    questionId: String = "",
) {
    val vm = koinViewModel<QuestionCreateQuestionVm>()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val questionUiState by vm.questionUiState.collectAsStateWithLifecycle()

    LaunchedEffect(questionId) {
        vm.initWithQuestionId(questionId)
    }

    QuestionCreateScreen(
        uiState = uiState,
        questionUiState = questionUiState,
        onEvent = { event: QuestionViewEvent -> vm.onEvent(event) },
        onCreateEvent = { vm.onCreateEvent(it) }
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
    onCreateEvent: (QuestionCreateQuestionEvent) -> Unit,
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
                // Categories selector (horizontal)
                CategoriesView(
                    selected = uiState.taxonomy.categoryKey,
                    onSelect = { cat ->
                        onCreateEvent(
                            QuestionCreateQuestionEvent.OnCategorySelected(
                                cat
                            )
                        )
                    }
                )
                // Subcategories selector for selected category (horizontal)
                SubcategoriesView(
                    categoryKey = uiState.taxonomy.categoryKey,
                    selectedSubKey = uiState.taxonomy.subCategoryKey,
                    onSelectTwoChoice = { sub ->
                        onCreateEvent(
                            QuestionCreateQuestionEvent.OnTwoChoiceSubSelected(
                                sub
                            )
                        )
                    },
                    onSelectThreeChoice = { sub ->
                        onCreateEvent(
                            QuestionCreateQuestionEvent.OnThreeChoiceSubSelected(
                                sub
                            )
                        )
                    }
                )

                QuestionTagsAreaContainer(
                    uiState = questionUiState,
                    onEvent = onEvent,
                    isCreateQuestion = true
                )

                if (GeneralSettings.isAdmin()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminAutoApproveCheckbox(
                        isChecked = uiState.isAutoApprove,
                        onCheckedChange = { checked ->
                            onCreateEvent(
                                QuestionCreateQuestionEvent.OnAutoApproveChanged(checked)
                            )
                        }
                    )
                }

                CreateQuestionYesNoView(
                    uiState = questionUiState,
                    onEvent = onEvent
                )
            }
        }
    )
}

@Composable
private fun AdminAutoApproveCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = "Auto-approve (Admin)",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CategoriesView(
    selected: String,
    onSelect: (QuestionCategories) -> Unit,
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scroll)
            .padding(horizontal = 8.dp)
    ) {
        listOf(
            QuestionCategories.TWO_CHOICE,
            QuestionCategories.THREE_CHOICE,
            // todo will be in next feature...
//            QuestionCategories.MULTI_CHOICE,
//            QuestionCategories.SCALE,
//            QuestionCategories.OPEN_ENDED,
        ).forEach { cat ->
            val isSel = (selected == cat.asKey())
            CategoryChip(text = cat.name, selected = isSel) { onSelect(cat) }
        }
    }
}

@Composable
fun SubcategoriesView(
    categoryKey: String,
    selectedSubKey: String?,
    onSelectTwoChoice: (TwoChoiceSubCategories) -> Unit,
    onSelectThreeChoice: (ThreeChoiceSubCategories) -> Unit,
) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scroll)
            .padding(horizontal = 8.dp)
    ) {
        when (categoryKey) {
            QuestionCategoryKeys.TWO_CHOICE -> {
                TwoChoiceSubCategories.values().forEach { sub ->
                    CategoryChip(text = sub.name, selected = (selectedSubKey == sub.asKey())) {
                        onSelectTwoChoice(sub)
                    }
                }
            }

            QuestionCategoryKeys.THREE_CHOICE -> {
                ThreeChoiceSubCategories.values().forEach { sub ->
                    CategoryChip(text = sub.name, selected = (selectedSubKey == sub.asKey())) {
                        onSelectThreeChoice(sub)
                    }
                }
            }

            else -> {
                // Not implemented for other categories
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionCreatePreview() {
    QuestionCreateScreen(
        uiState = QuestionCreateQuestionScreenUiState(),
        onEvent = {},
        questionUiState = QuestionViewUiState()
    ) { }
}
