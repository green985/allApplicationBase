package com.oyetech.composebase.projectQuestionsFeature.views.questions

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.models.questionProject.questionOperation.QuestionOptionCatalog
import kotlinx.collections.immutable.toImmutableList

@Preview(showBackground = true)
@Composable
fun Preview_Question_2Choice_YesNo() {
    val ui = QuestionViewUiState(
        isLoading = false,
        titleText = "Enable dark mode?",
        options = QuestionOptionCatalog.TwoChoice.YES_NO.toImmutableList(),
    )
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Question_2Choice_UpDown() {
    val ui = QuestionViewUiState(
        isLoading = false,
        titleText = "Trend vote",
        options = QuestionOptionCatalog.TwoChoice.UP_DOWN.toImmutableList(),
    )
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Question_2Choice_GoodBad() {
    val ui = QuestionViewUiState(
        isLoading = false,
        titleText = "Rate the update",
        options = QuestionOptionCatalog.TwoChoice.GOOD_BAD.toImmutableList(),
    )
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Question_3Choice_LowMedHigh() {
    val ui = QuestionViewUiState(
        isLoading = false,
        titleText = "Focus intensity",
        options = QuestionOptionCatalog.ThreeChoice.LOW_MED_HIGH.toImmutableList(),
    )
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Question_3Choice_AgreeNeutralDisagree() {
    val ui = QuestionViewUiState(
        isLoading = false,
        titleText = "This feature improved my productivity",
        options = QuestionOptionCatalog.ThreeChoice.AGREE_NEUTRAL_DISAGREE.toImmutableList(),
    )
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Factory_TwoChoice_YesNo() {
    val body =
        com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyFactory.buildQuestion(
            title = "Enable notifications?",
            taxonomy = com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyRef(
                categoryKey = com.oyetech.models.questionProject.questionOperation.QuestionCategoryKeys.TWO_CHOICE,
                subCategoryKey = com.oyetech.models.questionProject.questionOperation.TwoChoiceSubCategoryKeys.YES_NO
            )
        )
    val ui = body.toUiState()
    BaseQuestionView(uiState = ui, onEvent = {})
}

@Preview(showBackground = true)
@Composable
fun Preview_Factory_ThreeChoice_LowMedHigh() {
    val body =
        com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyFactory.buildQuestion(
            title = "Focus intensity",
            taxonomy = com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyRef(
                categoryKey = com.oyetech.models.questionProject.questionOperation.QuestionCategoryKeys.THREE_CHOICE,
                subCategoryKey = com.oyetech.models.questionProject.questionOperation.ThreeChoiceSubCategoryKeys.LOW_MED_HIGH
            )
        )
    val ui = body.toUiState()
    BaseQuestionView(uiState = ui, onEvent = {})
}
