package com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion

import com.oyetech.composebase.base.BaseEvent
import com.oyetech.composebase.base.BaseUIEvent
import com.oyetech.models.questionProject.questionOperation.QuestionCategories
import com.oyetech.models.questionProject.questionOperation.QuestionCategoryKeys
import com.oyetech.models.questionProject.questionOperation.QuestionTaxonomyRef
import com.oyetech.models.questionProject.questionOperation.ThreeChoiceSubCategories
import com.oyetech.models.questionProject.questionOperation.TwoChoiceSubCategories
import com.oyetech.models.questionProject.questionOperation.TwoChoiceSubCategoryKeys

data class QuestionCreateQuestionScreenUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
    val titleText: String = "",
    val descriptionText: String = "",
    val toolbarTitleText: String = "Create Question",
    val isSubmitEnabled: Boolean = false,
    val isSubmitted: Boolean = false,

    // Taxonomy selection for create screen
    val taxonomy: QuestionTaxonomyRef =
        QuestionTaxonomyRef(
            categoryKey = QuestionCategoryKeys.TWO_CHOICE,
            subCategoryKey = TwoChoiceSubCategoryKeys.YES_NO,
            taxonomyVersion = 1
        ),
)

sealed class QuestionCreateQuestionEvent : BaseEvent() {
    data class OnTitleChange(val text: String) : QuestionCreateQuestionEvent()
    data object OnSubmit : QuestionCreateQuestionEvent()
    data object OnRetry : QuestionCreateQuestionEvent()
    data object OnScreenOut : QuestionCreateQuestionEvent()

    // Taxonomy selection
    data class OnCategorySelected(val category: QuestionCategories) : QuestionCreateQuestionEvent()
    data class OnTwoChoiceSubSelected(val sub: TwoChoiceSubCategories) :
        QuestionCreateQuestionEvent()

    data class OnThreeChoiceSubSelected(val sub: ThreeChoiceSubCategories) :
        QuestionCreateQuestionEvent()
}

// UI Events (one-shot)
sealed class QuestionCreateQuestionUiEvent : BaseUIEvent() {
    data object OnSubmitSuccess : QuestionCreateQuestionUiEvent()
    data class OnSubmitError(val message: String) : QuestionCreateQuestionUiEvent()
}