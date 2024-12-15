package com.oyetech.helper.interceptors

import androidx.annotation.NonNull
import com.oyetech.cripto.apiParams.ApiPostParams
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.helper.refreshTokenHelper.RefreshTokenHelper
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
Created by Erdi Özbek
-4.03.2022-
-21:33-
 **/

class HeaderInterceptor constructor(
    var sharedOperationUseCase: SharedOperationUseCase,
    private var refreshTokenHelper: RefreshTokenHelper? = null,
) : Interceptor {

    var denemeFlag = false

    @Throws(IOException::class)
    override fun intercept(@NonNull chain: Interceptor.Chain): Response {
        Timber.d("Headerrrr intercepttt ")

        var token = sharedOperationUseCase.getToken()

        if (RefreshTokenDeneme.denemeFlag) {
            token = token.plus("asd")
        }

        val newRequest = chain.request().newBuilder()

        newRequest.addHeader(
            ApiPostParams.CLIENT_SECRET_KEY,
            sharedOperationUseCase.getClientSecretOrProduce()
        )

        newRequest.addHeader(
            ApiPostParams.CLIENT_UNIQ_ID,
            sharedOperationUseCase.getClientUniqSecretOrProduce()
        )

        if (token.isNotBlank()) {
            newRequest.addHeader(ApiPostParams.AUTH_KEY, token)
        }
        newRequest.addHeader("Connection", "close")
        newRequest.method(chain.request().method, chain.request().body)

        var response = chain.proceed(newRequest.build())
        refreshTokenHelper?.controlResponseStatusNotAcceptable(response)
        return response
    }
}
