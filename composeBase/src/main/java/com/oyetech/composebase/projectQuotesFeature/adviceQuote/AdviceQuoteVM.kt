package com.oyetech.composebase.projectQuotesFeature.adviceQuote;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.RemoveTag
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SelectAuthor
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SelectTag
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SetDummyData
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SetLoading
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.ShowError
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.SubmitQuote
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.ToggleExpandTagList
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.ToggleTruthForm
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateAuthorList
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateAuthorText
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateNoteToInspector
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateQuoteText
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateTagListLarge
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteEvent.UpdateTagListSmall
import com.oyetech.composebase.projectQuotesFeature.views.toolbar.QuoteToolbarState
import com.oyetech.composebase.sharedScreens.quotes.tagList.QuoteTagUiState
import com.oyetech.domain.quotesDomain.quotesData.QuoteDataOperationRepository
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.models.quotes.responseModel.QuotesTagResponseData
import com.oyetech.models.utils.helper.updateState
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
Created by Erdi Özbek
-29.01.2025-
-20:24-
 **/

class AdviceQuoteVM(
    private val repository: QuoteDataOperationRepository,
    private val dispatcher: AppDispatchers,
) : BaseViewModel(dispatcher) {

    val uiState: MutableStateFlow<AdviceQuoteUiState> = MutableStateFlow(AdviceQuoteUiState())

    val toolbarUiState =
        MutableStateFlow(QuoteToolbarState(title = LanguageKey.adviceQuote, showBackButton = true))

    val tmpSelectedTagList: List<QuoteTagUiState> = persistentListOf()

    init {
        populateFirstState()
    }

    fun populateFirstState() {
        val tagListSmall = QuotesTagResponseData.getTopicsList().subList(0, 4).map {
            QuoteTagUiState(it)
        }.toImmutableList()
        val tagListLarge = QuotesTagResponseData.getTopicsList()
            .subList(4, QuotesTagResponseData.getTopicsList().size).map {
                QuoteTagUiState(it)
            }.toImmutableList()

        uiState.updateState {
            copy(
                tagListSmall = tagListSmall,
                tagListLarge = tagListLarge
            )
        }
    }

    @Suppress("CyclomaticComplexMethod")
    fun onEvent(event: AdviceQuoteEvent) {

        when (event) {
            is UpdateQuoteText -> {
                uiState.updateState { copy(quoteText = event.quoteText) }
            }

            is UpdateAuthorText -> {
                uiState.updateState { copy(authorText = event.authorText) }
            }

            is UpdateNoteToInspector -> {
                uiState.updateState { copy(noteToInspector = event.noteToInspector) }
            }

            is ToggleExpandTagList -> {
                uiState.updateState { copy(isExpandTagList = event.isExpanded) }
            }

            is UpdateTagListSmall -> {
                uiState.updateState { copy(tagListSmall = event.tagList.toImmutableList()) }
            }

            is UpdateTagListLarge -> {
                uiState.updateState { copy(tagListLarge = event.tagList.toImmutableList()) }
            }

            is SelectTag -> {
                var selectedTaggList = uiState.value.selectedTagList.toPersistentList()
                selectedTaggList = selectedTaggList.add(event.tag)
                uiState.updateState {
                    copy(selectedTagList = selectedTaggList.toImmutableList())
                }
            }

            is RemoveTag -> {
                var selectedTaggList = uiState.value.selectedTagList.toPersistentList()
                selectedTaggList = selectedTaggList.remove(event.tag)
                uiState.updateState {
                    copy(selectedTagList = selectedTaggList.toImmutableList())
                }
            }

            is ToggleTruthForm -> {
                uiState.updateState {
                    copy(isCheckedTruthForm = event.isChecked)
                }
            }

            is SubmitQuote -> {
                submitQuote()
            }

            is SelectAuthor -> TODO()
            is SetLoading -> TODO()
            is ShowError -> TODO()
            is UpdateAuthorList -> TODO()
            SetDummyData -> {
                setDummyDatas()
            }
        }
        validateAndSetSendButtonState()
    }

    private fun validateAndSetSendButtonState() {
        uiState.updateState {
            copy(
                isSendButtonEnabled =
                isCheckedTruthForm &&
                        uiState.value.quoteText.isNotEmpty() &&
                        uiState.value.authorText.isNotEmpty() &&
                        uiState.value.selectedTagList.isNotEmpty()
            )
        }
    }

    fun setDummyDatas() {
        uiState.updateState {
            copy(
                quoteText = "quoteText",
                authorText = "authorText",
                selectedTagList = persistentListOf(
                    QuoteTagUiState("Anxiety"),
                    QuoteTagUiState("Death")
                ),
                noteToInspector = "noteToInspector",
                isCheckedTruthForm = true
            )
        }
    }

    private fun submitQuote() {
        viewModelScope.launch(getDispatcherIo()) {
            uiState.updateState { copy(isLoading = true) }
            val result = repository.submitQuote(
                quoteText = uiState.value.quoteText,
                authorText = uiState.value.authorText,
                tags = uiState.value.selectedTagList.map { it.tagName },
                noteToInspector = uiState.value.noteToInspector,
                isCheckedTruthForm = uiState.value.isCheckedTruthForm
            ).asResult().collectLatest {
                it.fold(
                    onSuccess = {
                        uiState.updateState {
                            copy(
                                isLoading = false,
                                quoteText = "",
                                authorText = "",
                                selectedTagList = persistentListOf(),
                                noteToInspector = "",
                                isCheckedTruthForm = false
                            )
                        }
                    },
                    onFailure = {
                        uiState.updateState {
                            copy(
                                isLoading = false
                            )

                        }
                    })
            }
        }
    }
}