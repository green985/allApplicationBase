package com.oyetech.composebase.sharedScreens.userList;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.RegisterToUserList
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Erdi Özbek
-26.02.2025-
-20:27-
 **/

class UserListVm(
    appDispatchers: AppDispatchers,
    private val firebaseUserListOperationRepository: FirebaseUserListOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(UserListUiState())

    init {
    }

    fun onEvent(event: UserListEvent) {
        when (event) {
            RegisterToUserList -> {
                viewModelScope.launch(getDispatcherIo()) {
                    firebaseUserListOperationRepository.addUserToUserList().asResult()
                        .collectLatest {
                            Timber.d("User added to user list")
                        }
                }

            }
        }
    }
}