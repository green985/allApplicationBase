package com.oyetech.models.entity.user

import com.oyetech.models.utils.states.SocketUserOperation

/**
Created by Erdi Özbek
-23.06.2022-
-14:45-
 **/

data class UserTypingSendingOperationStatusData(
    var userId: Long,
    var userOperation: SocketUserOperation
)
