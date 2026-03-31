package com.oyetech.remote.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.models.utils.const.HelperConstant.DEFAULT_TIMEOUT
import com.oyetech.remote.questionRemote.AuthInterceptor
import com.oyetech.remote.questionRemote.QuestionSupabaseApi
import com.oyetech.remote.questionRemote.QuestionSupabaseDataSource
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit.NANOSECONDS

object QuestionSupabaseRemote {

    const val QUESTION_SUPABASE_BASE_URL = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions/"

    fun createQuestionSupabaseRemoteModule(baseUrl: String = QUESTION_SUPABASE_BASE_URL) = module {
        singleOf(::AuthInterceptor)

        single {
            Timber.d("Providing QuestionSupabaseRemote OkHttpClient")
            OkHttpClient.Builder()
                .readTimeout(DEFAULT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT, java.util.concurrent.TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(0, 1, NANOSECONDS))
                .retryOnConnectionFailure(true)
                .addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                        redactHeader("Authorization")
                    }
                )
                .addInterceptor(get<AuthInterceptor>())
                .build()
        }

        single {
            Retrofit.Builder().apply {
                client(get())
                Timber.d("QuestionSupabaseRemote retrofit baseUrl = $baseUrl")
                baseUrl(baseUrl)
                addCallAdapterFactory(CoroutineCallAdapterFactory())
                addConverterFactory(MoshiConverterFactory.create(get()).asLenient())
            }.build()
        }

        single { get<Retrofit>().create(QuestionSupabaseApi::class.java) }
        single { QuestionSupabaseDataSource(get()) }
    }
}
