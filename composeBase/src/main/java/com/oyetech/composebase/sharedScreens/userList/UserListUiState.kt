package com.oyetech.composebase.sharedScreens.userList

/**
Created by Erdi Özbek
-26.02.2025-
-20:26-
 **/

data class UserListUiState(
    val isLoading: Boolean = false,
    val errorText: String = "",
)

sealed class UserListEvent {
    //    data class Idle(val data: Int) : UserListEvent()
//    object Idlee : UserListEvent()
    object RegisterToUserList : UserListEvent()
}
