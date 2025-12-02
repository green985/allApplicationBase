package com.oyetech.remote.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.oyetech.remote.questionRemote.AuthInterceptor
import com.oyetech.remote.questionRemote.QuestionSupabaseApi
import com.oyetech.remote.questionRemote.QuestionSupabaseDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

object QuestionSupabaseRemote {

    const val QUESTION_SUPABASE_BASE_URL = "https://uduwhuvgdcacvdhzheyi.supabase.co/functions/"

    fun createQuestionSupabaseRemoteModule(baseUrl: String = QUESTION_SUPABASE_BASE_URL) = module {
        single { AuthInterceptor(get()) }

        single {
            Timber.d("Providing QuestionSupabaseRemote OkHttpClient")
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
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
