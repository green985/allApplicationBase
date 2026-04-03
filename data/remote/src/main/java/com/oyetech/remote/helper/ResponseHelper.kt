package com.oyetech.remote.helper

import com.oyetech.models.GenericResponse
import com.oyetech.models.errors.ErrorMessage.throwError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
Created by Erdi Özbek
-2.03.2022-
-04:31-
 **/

@Suppress("TooGenericExceptionCaught")
suspend fun <T> Response<T>.interceptNormalResponse(): Flow<T> {
    try {
        val resultt = this
        val flow = flow<T> {
            val isSuccess = resultt.isSuccessful
            if (isSuccess) {
                val result = resultt.body()
                if (result == null) {
                    throw IOException(throwError(null))
                }

                val response = result
                if (response == null) {
                    // Response null, problem backend
                    throw IOException(throwError("erororororororo"))
                }
                // finally result are not null and success
                emit(response)
            } else {
                throw IOException(throwError(null))
            }
        }
        return flow
    } catch (e: Exception) {
        e.printStackTrace()
    }
    throw IOException()

    return flow { throw IOException(throwError(null)) }
}

@Suppress("TooGenericExceptionCaught")
fun <T> Response<GenericResponse<T>>.interceptGenericResponseWithoutFlowWithoutNull(): T {
    try {
        val resultt = this
        val isSuccess = resultt.isSuccessful
        if (isSuccess) {
            val result = resultt.body()
            if (result == null) {
                throw IOException(throwError(null))
            }
            if (!result.resultStatus) {
                // result status false, throw error
                Timber.d("resuylttt = " + result.toString())
                throw IOException(throwError(result.resultMessage))
            }

            val response = result.resultObject
            if (response == null) {
                // Response null, problem backend
                throw IOException(throwError(result.resultMessage))
            }

            // finally result are not null and success
            return response
        } else {
            val code = resultt.code()
            val result = resultt.body()
            val errorMessage = if (code == 401) {
                "401 Unauthorized - Token may be expired or invalid"
            } else {
                result?.resultMessage ?: "HTTP error code: $code"
            }

            throw IOException(throwError(errorMessage))
        }
    } catch (e: Exception) {
        Timber.d("exception = " + e)
        e.printStackTrace()
        throw IOException(e.message)
    }
    throw IOException()
}

@Suppress("TooGenericExceptionCaught")
fun <T> interceptGenericResponseTrueForm(apiCall: suspend () -> Response<GenericResponse<T>>): Flow<T> {
    return flow {
        emit(apiCall.invoke())
    }.flatMapMerge {
        val ttt = it.interceptGenericResponseWithoutFlowWithoutNull()
        flowOf(ttt)
    }
}

@Suppress("TooGenericExceptionCaught")
fun <T> interceptTrueForm(apiCall: suspend () -> Response<T>): Flow<T> {
    return flow {
        emit(apiCall.invoke())
    }.flatMapMerge {
        val ttt = it.interceptNormalResponse()
        ttt
    }
}

/*

@Suppress("TooGenericExceptionCaught")
suspend fun <T> Deferred<Response<GenericResponse<T>>>.interceptGenericResponse(): Flow<T> {
    try {
        var resultt = this.await()
        var flow = flow<T> {
            var isSuccess = resultt.isSuccessful
            if (isSuccess) {
                var result = resultt.body()
                if (result == null) {
                    throw IOException(throwError(null))
                }

                if (!result.resultStatus) {
                    // result status false, throw error
                    throw IOException(throwError(result.resultMessage))
                }

                val response = result.resultObject
                if (response == null) {
                    // Response null, problem backend
                    throw IOException(throwError(result.resultMessage))
                }
                // finally result are not null and success
                emit(response)
            } else {
                throw IOException(throwError(null))
            }
        }
        return flow
    } catch (e: Exception) {
        e.printStackTrace()
    }
    throw IOException()

    return flow { throw IOException(throwError(null)) }
}

@Suppress("TooGenericExceptionCaught")
fun <T> Response<GenericResponse<T>>.interceptGenericResponseWithoutFlow(): T? {
    try {
        var resultt = this
        var isSuccess = resultt.isSuccessful
        if (isSuccess) {
            var result = resultt.body()
            if (result == null) {
                throw IOException(throwError(null))
            }

            if (!result.resultStatus) {
                // result status false, throw error
                throw IOException(throwError(result.resultMessage))
            }

            val response = result.resultObject
            if (response == null) {
                // Response null, problem backend
                throw IOException(throwError(result.resultMessage))
            }

            // finally result are not null and success
            return response
        }
    } catch (e: Exception) {
        Timber.d("exception = " + e)
        e.printStackTrace()
    }
    return null
}

 */
