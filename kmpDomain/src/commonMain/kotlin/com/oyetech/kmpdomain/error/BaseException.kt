package com.oyetech.kmpdomain.error

open class BaseException(
    override val message: String,
    cause: Throwable? = null,
) : Exception(message, cause)

class HttpStatusException(
    val statusCode: Int,
    message: String,
) : BaseException(message)

class ResponseContractException(message: String) : BaseException(message)
