package com.oyetech.remote.di

import android.content.Context
import android.content.pm.ApplicationInfo
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.remote.wallpaperRemote.services.ApiBibleService
import com.oyetech.remote.wallpaperRemote.services.BibleService
import com.oyetech.remote.wallpaperRemote.services.HideApiService
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.NANOSECONDS

/**
Created by Erdi Özbek
-28.02.2022-
-22:18-
 **/

fun createRemoteModule(baseUrl: String, apiBibleServiceUrl: String) = module {

    single {
        Retrofit.Builder().apply {
            baseUrl(baseUrl)
            addCallAdapterFactory(CoroutineCallAdapterFactory())
            addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
            // addConverterFactory(GsonConverterFactory.create(get()))
            client(get<OkHttpClient>())
        }.build()
    }


    single { get<Retrofit>().create(BibleService::class.java) }


    single {
        var builder = OkHttpClient.Builder().apply {
            connectTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            // protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            connectionPool(ConnectionPool(0, 1, NANOSECONDS))
            retryOnConnectionFailure(true)
            if (get<Context>().isDebug()) {
                interceptors().add(get<HttpLoggingInterceptor>())
            }
        }
        var client = builder.build()
        Retrofit.Builder().apply {
            baseUrl(baseUrl)
            addCallAdapterFactory(CoroutineCallAdapterFactory())
            addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
            // addConverterFactory(GsonConverterFactory.create(get()))
            client(client)
        }.build()

        get<Retrofit>().create(HideApiService::class.java)
    }


    single {
        var builder = OkHttpClient.Builder().apply {
            connectTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(HelperConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            // protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
            connectionPool(ConnectionPool(0, 1, NANOSECONDS))
            retryOnConnectionFailure(true)
            if (get<Context>().isDebug()) {
                interceptors().add(get<HttpLoggingInterceptor>())
            }
        }
        var client = builder.build()
        var service = Retrofit.Builder().apply {
            baseUrl(apiBibleServiceUrl)
            addCallAdapterFactory(CoroutineCallAdapterFactory())
            addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
            // addConverterFactory(GsonConverterFactory.create(get()))
            client(client)
        }.build().create(ApiBibleService::class.java)

        service
    }

}

fun Context.isDebug(): Boolean {
    return this.getApplicationInfo().flags and
            ApplicationInfo.FLAG_DEBUGGABLE !== 0
}