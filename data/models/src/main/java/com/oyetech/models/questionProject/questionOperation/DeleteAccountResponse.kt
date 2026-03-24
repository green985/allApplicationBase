package com.oyetech.models.questionProject.questionOperation

import androidx.annotation.Keep

@Keep
data class DeleteAccountResponse(
    val message: String = "",
    val deletedUserId: String = "",
)

