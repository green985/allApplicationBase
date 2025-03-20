package com.oyetech.composebase.sharedScreens.userList

import com.oyetech.composebase.base.BaseEvent

/**
Created by Erdi Özbek
-26.02.2025-
-20:26-
 **/

data class UserListUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
)

sealed class UserListEvent : BaseEvent() {
    //    data class Idle(val data: Int) : UserListEvent()
//    object Idlee : UserListEvent()
    object RegisterToUserList : UserListEvent()
    object RemoveUserFromList : UserListEvent()
}
