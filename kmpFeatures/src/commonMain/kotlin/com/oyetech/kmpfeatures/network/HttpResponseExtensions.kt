package com.oyetech.kmpfeatures.network

import com.oyetech.kmpdomain.error.HttpStatusException
import com.oyetech.kmpdomain.error.ResponseContractException
import com.oyetech.kmpmodels.response.GenericResponse
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend inline fun <reified T> HttpResponse.bodyOrError(): T {
    val response = body<GenericResponse<T>>()
    if (status.value !in 200..299) {
        throw HttpStatusException(
            statusCode = status.value,
            response.message.ifBlank { "HTTP error: ${status.value}" },
        )
    }
    if (!response.status) {
        throw HttpStatusException(
            statusCode = status.value,
            response.message.ifBlank { "Request failed" },
        )
    }
    return response.data
        ?: throw ResponseContractException(
            response.message.ifBlank { "Response data is empty" },
        )
}
