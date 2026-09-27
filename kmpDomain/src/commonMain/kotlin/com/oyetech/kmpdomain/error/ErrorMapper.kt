package com.oyetech.kmpdomain.error

import com.oyetech.kmpmodels.ui.state.ErrorCode
import com.oyetech.kmpmodels.ui.state.UiError

class ErrorMapper {
    fun map(error: Throwable): UiError {
        if (error is BaseException) {
            val code = when (error) {
                is HttpStatusException -> when (error.statusCode) {
                    401, 403 -> ErrorCode.Unauthorized
                    404 -> ErrorCode.NotFound
                    in 400..499 -> ErrorCode.Validation
                    else -> ErrorCode.Unknown
                }

                is ResponseContractException -> ErrorCode.Unknown
                else -> ErrorCode.Unknown
            }
            return UiError(
                message = error.message.safeMessage(),
                code = code,
            )
        }

        val typeName = error::class.simpleName.orEmpty()
        if (
            typeName.contains("Timeout", ignoreCase = true) ||
            typeName.contains("Connect", ignoreCase = true) ||
            typeName.contains("Socket", ignoreCase = true) ||
            typeName.contains("Network", ignoreCase = true)
        ) {
            return UiError(
                message = "Ağ bağlantısı kurulamadı.",
                code = ErrorCode.Network,
            )
        }

        return UiError(
            message = "Beklenmeyen bir hata oluştu.",
            code = ErrorCode.Unknown,
        )
    }

    private fun String?.safeMessage(): String =
        this?.takeIf { it.isNotBlank() } ?: "İstek tamamlanamadı."
}
