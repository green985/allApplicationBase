package com.oyetech.remote.di

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.remote.wallpaperRemote.services.WallpaperServiceApi
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.TimeUnit.SECONDS

/**
Created by Erdi Özbek
-7.02.2024-
-15:18-
 **/

object WallpaperRemoteModule {

    fun createWallpaperAppRemoteModule(baseUrl: String) = module {

        single {
            var builder = OkHttpClient.Builder().apply {
                connectTimeout(HelperConstant.DEFAULT_TIMEOUT, SECONDS)
                readTimeout(HelperConstant.DEFAULT_TIMEOUT, SECONDS)
                writeTimeout(HelperConstant.DEFAULT_TIMEOUT, SECONDS)
                // protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                connectionPool(ConnectionPool(0, 1, NANOSECONDS))
                retryOnConnectionFailure(true)
                if (get<Context>().isDebug()) {
                    interceptors().add(get<HttpLoggingInterceptor>())
                }
            }
            var client = builder.build()
            Timber.d("baseUrllll = " + baseUrl)
            var retrofit = Builder().apply {
                baseUrl(baseUrl)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
                // addConverterFactory(GsonConverterFactory.create(get()))
                client(client)
            }.build()

            retrofit.create(WallpaperServiceApi::class.java)
        }

    }

}