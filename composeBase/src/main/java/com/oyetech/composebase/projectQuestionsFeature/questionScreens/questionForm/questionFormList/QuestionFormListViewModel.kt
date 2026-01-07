package com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.questionFormList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class QuestionFormListViewModel(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val questionSupabaseRepository: QuestionSupabaseRepository,
) : BaseViewModel(appDispatchers) {

    private val _uiState = MutableStateFlow(QuestionFormListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getCatalogList()
    }

    fun onEvent(event: QuestionFormListEvent) {
        when (event) {
            is QuestionFormListEvent.OnCatalogItemClick -> handleCatalogItemClick(event.formId)
            is QuestionFormListEvent.OnRefresh -> getCatalogList()
        }
    }

    private fun getCatalogList() {
        viewModelScope.launch(getDispatcherIo()) {
            _uiState.updateState { copy(isLoading = true) }
            questionSupabaseRepository.getCatalogList("")
                .asResult()
                .collectLatest { response ->
                    response.fold(
                        onSuccess = { resp ->
                            val catalogItems =
                                resp.catalogs.map { it.toUiState() }.toImmutableList()
                            _uiState.updateState {
                                copy(
                                    catalogList = catalogItems,
                                    isLoading = false
                                )
                            }
                        },
                        onFailure = { error ->
                            Timber.e(error, "Error loading catalog list")
                            _uiState.updateState {
                                copy(
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
        }
    }

    private fun handleCatalogItemClick(formId: String) {
        val route =
            "${QuestionAppProjectRoutes.QuestionFormScreen.route}?formId=$formId"
        navigationUseCase.navigateTo(route)
    }
}
