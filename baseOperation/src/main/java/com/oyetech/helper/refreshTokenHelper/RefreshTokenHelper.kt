package com.oyetech.helper.refreshTokenHelper

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.helper.BaseUrlConfigHelper
import com.oyetech.helper.authOperationHelper.AuthOperationBodyHelper
import com.oyetech.helper.interceptors.HeaderInterceptor
import com.oyetech.models.entity.GenericResponse
import com.oyetech.models.entity.auth.AuthRequestResponse
import com.oyetech.models.postBody.auth.RefreshAuthRequestBody
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.const.HelperConstant.DEFAULT_TIMEOUT
import com.oyetech.models.utils.moshi.DefaultIfNullFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
Created by Erdi Özbek
-7.06.2022-
-15:45-
 **/

class RefreshTokenHelper(
    private var authOperationBodyHelper: AuthOperationBodyHelper,
    private var sharedOperationUseCase: SharedOperationUseCase,
    private var context: Context,
) {
    init {
        Timber.d("RefreshTokenHelper initt")
    }

    var tokenRefreshed = false
    fun getRefreshTokenResponse(): Response<GenericResponse<AuthRequestResponse>>? {
        synchronized(sharedOperationUseCase) {
            if (sharedOperationUseCase.getRefreshToken().isNullOrBlank()) {
                Timber.d("refresh null")
                return null
            }
            var service = makeRetrofitApi()
            var refreshAuthRequestBody = authOperationBodyHelper.generateRefreshAuthOperation()
            try {
                var tokenData = service.getTokenDataResponse(refreshAuthRequestBody).execute()
                return tokenData
            } catch (e: Exception) {
                return null
            }
        }
    }

    private fun makeRetrofitApi(): RefreshTokenService {
        var moshi =
            Moshi.Builder()
                .add(DefaultIfNullFactory())
                .addLast(KotlinJsonAdapterFactory())
                .build()

        val dispatcher = Dispatcher()
        dispatcher.maxRequestsPerHost = 1
        dispatcher.maxRequests = 1

        var httpClientBuilder = OkHttpClient.Builder().apply {
            connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            dispatcher(dispatcher)
            connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
            addInterceptor(HeaderInterceptor(sharedOperationUseCase))
            var logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            if (context.isDebug()) {
                interceptors().add(logging)
            }
        }
        var httpClient = httpClientBuilder.build()

        var builder = Retrofit.Builder().apply {
            baseUrl(BaseUrlConfigHelper.getBaseUrl())
            addConverterFactory(MoshiConverterFactory.create(moshi))
            client(httpClient)
        }.build()

        return builder.create(RefreshTokenService::class.java)
    }

    fun saveTokenSynchronous(refreshResponse: Response<GenericResponse<AuthRequestResponse>>?) {
        if (refreshResponse == null) {
            Timber.d("RefreshToken null")
            return
        }
        if (refreshResponse.body() == null) {
            Timber.d("refreshResponse.body null")
            return
        }
        if (refreshResponse.body()?.resultObject == null) {
            Timber.d("resultObject.body null")
            return
        }

        sharedOperationUseCase.saveUserTokenData(refreshResponse.body()!!.resultObject!!)
        tokenRefreshed = true
        Timber.d("Token already refreshed")
        // make request with newHeader
        Handler(Looper.getMainLooper()).postDelayed({
            Timber.d("tokenRefreseh false oldu")
            tokenRefreshed = false
        }, HelperConstant.REFRESH_TOKEN_FLAG_TIMEOUT)
    }

    fun logoutUserAndNavigateLoginActivity() {
        tokenRefreshed = false
        sharedOperationUseCase.removeSharedPrefValues()
        //ActivityStartHelper.startLoginActivityWithClearStack(context)
    }

    fun controlResponseStatusNotAcceptable(response: okhttp3.Response) {
        var code = response.code
        if (code == HelperConstant.HTTP_NOT_ACCEPTABLE) {
            Timber.d("not acceptable...")
            logoutUserAndNavigateLoginActivity()
            response.close()
            Timber.d("navigate complete...")
            throw IOException("io error")
        }
    }
}

interface RefreshTokenService {

    @POST("authentication/refresh")
    fun getTokenDataResponse(
        @Body params: RefreshAuthRequestBody,
    ): Call<GenericResponse<AuthRequestResponse>>
}
