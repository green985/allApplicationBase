package com.oyetech.kmpmodels.ui.state

data class UiError(
    val message: String,
    val code: ErrorCode = ErrorCode.Unknown,
)

enum class ErrorCode {
    Unauthorized,
    Validation,
    NotFound,
    Network,
    Unknown,
}

sealed interface OperationState {
    data object Idle : OperationState
    data object Loading : OperationState
    data class Error(val error: UiError) : OperationState
}
