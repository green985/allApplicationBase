package com.oyetech.remote.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.remote.RadioApiService
import com.oyetech.remote.radioRemote.dataSourcee.CountryTagDataSource
import com.oyetech.remote.radioRemote.dataSourcee.StationListDataSource
import com.oyetech.remote.randomOperationRemote.RandomOperationApiService
import com.oyetech.remote.randomOperationRemote.RandomOperationDataSource
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

/**
Created by Erdi Özbek
-27.11.2022-
-23:26-
 **/

object RadioModuleDI {

    fun createRemoteModule(baseUrl: String) = module {

        single {
            Retrofit.Builder().apply {
                client(get<OkHttpClient>())
                Timber.d("dnsssssss   retroffiifif == ")
                baseUrl(baseUrl)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
                // addConverterFactory(GsonConverterFactory.create(get()))
            }.build()
        }

        single { get<Retrofit>().create(RadioApiService::class.java) }

        // single { AuthDataSource(get()) }

        single { StationListDataSource(get()) }
        single { CountryTagDataSource(get()) }
    }
}

object RandomOperationModuleDI {

    fun createRemoteModuleForRandomOperation(baseUrl: String) = module {

        single {
            Retrofit.Builder().apply {
                client(get<OkHttpClient>())
                Timber.d("dnsssssss   retroffiifif == ")
                baseUrl(baseUrl)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
                // addConverterFactory(GsonConverterFactory.create(get()))
            }.build()
        }

        single { get<Retrofit>().create(RandomOperationApiService::class.java) }

        single { RandomOperationDataSource(get()) }
    }
}
