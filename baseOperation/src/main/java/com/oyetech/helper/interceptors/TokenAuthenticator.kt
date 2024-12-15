package com.oyetech.helper.interceptors

import android.content.Context
import com.oyetech.cripto.apiParams.ApiPostParams
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.helper.refreshTokenHelper.RefreshTokenHelper
import com.oyetech.models.utils.const.HelperConstant
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import java.io.IOException

/**
Created by Erdi Özbek
-4.04.2022-
-00:14-
 **/

class TokenAuthenticator constructor(
    private var context: Context,
    private var sharedOperationUseCase: SharedOperationUseCase,
    private var refreshTokenHelper: RefreshTokenHelper,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request {
        if (response.request.url.toString().contains(HelperConstant.hereRequestUrl)) {
            return response.request
        }


        synchronized(context) {
            var runBlocking = runBlocking {
                Timber.d("TokenAuthenticator response1 == " + response.request.url)

                if (response.code == HelperConstant.HTTP_AUTH_ERROR) {
                    // call refresh token...

                    if (refreshTokenHelper.tokenRefreshed) {
                        // token refreshed. not called again...
                        return@runBlocking initNewTokenHeaderAndResume(response)
                    }

                    var refreshTokenResult = refreshTokenHelper.getRefreshTokenResponse()
                    if (refreshTokenResult == null) {
                        refreshTokenHelper.logoutUserAndNavigateLoginActivity()
                        response.close()
                        throw IOException("io error")
                        return@runBlocking response.request
                    }

                    when (refreshTokenResult.code()) {
                        HelperConstant.HTTP_SUCCESS -> {
                            refreshTokenHelper.saveTokenSynchronous(refreshTokenResult)
                            return@runBlocking initNewTokenHeaderAndResume(response)
                        }

                        HelperConstant.HTTP_AUTH_ERROR -> {
                            refreshTokenHelper.logoutUserAndNavigateLoginActivity()
                            response.close()
                            return@runBlocking response.request
                        }

                        else -> {
                            response.close()
                            return@runBlocking response.request
                        }
                    }
                }
                return@runBlocking response.request
            }

            return runBlocking
        }
    }

    private fun initNewTokenHeaderAndResume(response: Response): Request {
        return response.request.newBuilder()
            .header(
                ApiPostParams.CLIENT_SECRET_KEY,
                sharedOperationUseCase.getLastGuid()
            )
            .header(
                ApiPostParams.AUTH_KEY, sharedOperationUseCase.getToken()
            )
            .build()
    }
}

object RefreshTokenDeneme {
    var denemeFlag = false
}
