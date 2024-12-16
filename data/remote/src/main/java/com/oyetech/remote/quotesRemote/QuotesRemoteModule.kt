package com.oyetech.remote.quotesRemote

import android.content.Context
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.remote.di.isDebug
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit.NANOSECONDS
import java.util.concurrent.TimeUnit.SECONDS

/**
Created by Erdi Özbek
-16.12.2024-
-22:10-
 **/

object QuotesRemoteModule {

    const val QUOTES_BASE_URL = "https://zenquotes.io/api/"

    fun createZenQuotesRemoteModule() = module {
        val baseUrl = QUOTES_BASE_URL
        single {
            val builder = OkHttpClient.Builder().apply {
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
            val client = builder.build()
            val retrofit = Builder().apply {
                baseUrl(baseUrl)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(MoshiConverterFactory.create(get()))
                client(client)
            }.build()

            retrofit.create(ZenQuotesApi::class.java)
        }

    }

}
