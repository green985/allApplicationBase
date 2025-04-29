package com.oyetech.composebase.sharedScreens.userList;

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.baseGenericList.BaseListViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.RegisterToUserList
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.RemoveUserFromList
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import com.oyetech.composebase.sharedScreens.userList.item.mapToUiState
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
) : BaseListViewModel<UserListItemUiState>(appDispatchers) {
    val uiState = MutableStateFlow(UserListUiState())

    override val listViewState: MutableStateFlow<GenericListState<UserListItemUiState>> =
        MutableStateFlow(
            GenericListState<UserListItemUiState>(
                isRefreshEnable = true,
                dataFlow = firebaseUserListOperationRepository.getRandomUsersFromDatabase()
                    .mapToUiState(),
                triggerRefresh = { refreshList() }
            )
        )

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

            RemoveUserFromList -> {
                viewModelScope.launch(getDispatcherIo()) {
                    firebaseUserListOperationRepository.removeUserFromUserList().asResult()
                        .collectLatest {
                            Timber.d("User removed from user list")
                            refreshList()
                        }
                }
            }
        }
    }
}