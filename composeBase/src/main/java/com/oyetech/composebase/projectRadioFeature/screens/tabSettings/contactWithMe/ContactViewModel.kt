package com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.domain.repository.helpers.FirebaseContactWithMeOperationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ContactViewModel(
    appDispatchers: com.oyetech.tools.coroutineHelper.AppDispatchers,
    private val firebaseDBOperationRepository: FirebaseContactWithMeOperationRepository,
) : BaseViewModel(appDispatchers) {
    private val _uiState = MutableStateFlow(ContactUIState())
    val uiState: StateFlow<ContactUIState> = _uiState

    fun onEvent(event: ContactUIEvent) {
        when (event) {
            is ContactUIEvent.UpdateName -> {
                _uiState.updateState { copy(name = event.name) }
            }

            is ContactUIEvent.UpdateEmail -> {
                val isValid =
                    event.email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS.matcher(event.email)
                        .matches()
                _uiState.updateState { copy(email = event.email, isEmailValid = isValid) }
            }

            is ContactUIEvent.UpdateMessage -> {
                _uiState.updateState {
                    copy(
                        message = event.message,
                        isDescriptionEmpty = event.message.isEmpty()
                    )
                }
            }

            ContactUIEvent.Submit -> {
                val data = uiState.value
                if (data.isEmailValid && !data.isDescriptionEmpty) {
                    sendContactWithMeData(data)
                }
            }
        }
    }

    private fun sendContactWithMeData(data: ContactUIState) {
        firebaseDBOperationRepository.sendFeedback(
            email = data.email, message = data.message, name = data.name
        )
        viewModelScope.launch(getDispatcherIo()) {
            firebaseDBOperationRepository.sendFeedbackOperationStateFlow.collectLatest {
                _uiState.updateState {
                    Timber.d(" ContactViewModel sendContactWithMeData: $it")
                    ContactUIState(isContactWasSent = true)
                }
            }
        }


    }
}
