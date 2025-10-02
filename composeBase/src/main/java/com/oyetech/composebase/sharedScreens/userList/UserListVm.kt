package com.oyetech.composebase.sharedScreens.userList

import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.baseGenericList.GenericListState
import com.oyetech.composebase.base.baseGenericList.makeEmptyListState
import com.oyetech.composebase.base.baseGenericList.setList
import com.oyetech.composebase.base.baseGenericList.updateErrorInitial
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.sharedScreens.navigation.ScreenKey
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.OnUserClick
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.RegisterToUserList
import com.oyetech.composebase.sharedScreens.userList.UserListEvent.RemoveUserFromList
import com.oyetech.composebase.sharedScreens.userList.item.UserListItemUiState
import com.oyetech.composebase.sharedScreens.userList.item.mapToUiState
import com.oyetech.domain.repository.firebase.FirebaseUserListOperationRepository
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import com.oyetech.tools.coroutineHelper.asResult
import kotlinx.coroutines.delay
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
    private val navigationUseCase: NavigationUseCase,
    private val firebaseUserListOperationRepository: FirebaseUserListOperationRepository,
) : BaseViewModel(appDispatchers) {
    val uiState = MutableStateFlow(UserListUiState())

    val listViewState: MutableStateFlow<GenericListState<UserListItemUiState>> =
        MutableStateFlow(
            GenericListState<UserListItemUiState>(
                isRefreshEnable = true,
                dataFlow = firebaseUserListOperationRepository.getRandomUsersFromDatabase()
                    .mapToUiState(),
                triggerRefresh = { refreshList() }
            )
        )

    init {
        loadList()
    }

    fun refreshList() {
        listViewState.updateState { copy(isRefreshing = true) }
        loadList(isFromRefresh = true)
    }

    private fun loadList(isFromRefresh: Boolean = false) {
        if (!isFromRefresh) {
            listViewState.updateState { copy(isLoadingInitial = true) }
        }

        viewModelScope.launch(getDispatcherIo()) {
            delay(2000)
            listViewState.value.dataFlow?.asResult()?.collectLatest { result ->
                Timber.d("ListResulttt== : ${result.isSuccess}")
                result.fold({ list ->
                    if (list.isEmpty()) {
                        listViewState.makeEmptyListState()
                    } else {
                        listViewState.setList(list)
                    }
                }, {
                    listViewState.updateErrorInitial(it)
                })
            }
        }
    }

    override fun onEvent(event: Any) {
        if (event is UserListEvent) {
            when (event) {
                RegisterToUserList -> {
                    viewModelScope.launch(getDispatcherIo()) {
                        firebaseUserListOperationRepository.addUserToUserList().asResult()
                            .collectLatest {
                                Timber.d("User added to user list")
                            }
                    }

                }

                UserListEvent.RefreshUserList -> {
                    refreshList()
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

                is OnUserClick -> {
                    val itemDetail = listViewState.value.items[event.index]
                    navigationUseCase.navigate(
                        QuestionAppProjectRoutes.MessageDetail.withArgs(
                            ScreenKey.receiverUserId to itemDetail.userId,
                        )
                    )

                }
            }
        }
    }
}