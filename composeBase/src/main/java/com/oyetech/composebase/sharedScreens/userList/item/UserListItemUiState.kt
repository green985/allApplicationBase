package com.oyetech.composebase.sharedScreens.userList.item

import com.oyetech.models.firebaseModels.userList.FirebaseUserListModel
import com.oyetech.models.utils.helper.TimeFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
Created by Erdi Özbek
-26.02.2025-
-20:37-
 **/

data class UserListItemUiState(
    val id: String,
    val userId: String,
    val username: String,
    val joinedAtString: String,
)

sealed class UserListItemEvent {
//    data class Idle(val data: Int) : UserListItemEvent()
//    object Idlee : UserListItemEvent()
}

fun Flow<List<FirebaseUserListModel>>.mapToUiState(): Flow<List<UserListItemUiState>> {
    return this.map {
        it.map {
            UserListItemUiState(
                id = it.documentId,
                userId = it.userId,
                username = it.username,
                joinedAtString = TimeFunctions.getDateFromLongWithHour(it.joinedAt?.time ?: 0L)
            )
        }
    }
}