package com.oyetech.radioeveryonee.main.koins

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.oyetech.domain.helper.isDebug
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.repository.helpers.SharedHelperRepository
import com.oyetech.helper.authOperationHelper.AuthOperationBodyHelper
import com.oyetech.helper.interceptors.HeaderInterceptor
import com.oyetech.helper.interceptors.TokenAuthenticator
import com.oyetech.helper.language.LanguageHelper
import com.oyetech.helper.onlineOfflineHelper.OnlineOfflineStatusHelper
import com.oyetech.helper.refreshTokenHelper.RefreshTokenHelper
import com.oyetech.helper.sharedPref.SharedHelper
import com.oyetech.helper.sharedPref.SharedPrefRepository
import com.oyetech.helper.sharedPref.SharedPrefRepositoryImp
import com.oyetech.helper.unreadMessageHelper.UnreadMessageCalculatorHelper
import com.oyetech.helper.updateHelper.ForceUpdateHelper
import com.oyetech.models.utils.const.HelperConstant.DEFAULT_TIMEOUT
import com.oyetech.models.utils.moshi.DefaultIfNullFactory
import com.oyetech.radioeveryonee.main.BaseApplication
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

object KoinHelperInits {

    private var BASE_SHARED_PREF_KEY = "BASE_SHARED_PREF_KEY"

    val HelperModule = module {
        single {
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .setLenient()
                .create()
        }
        single {

            var s =
                Moshi.Builder()
                    .add(DefaultIfNullFactory())
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
            s
        }

        single {
            androidContext().getSharedPreferences(
                BASE_SHARED_PREF_KEY,
                Context.MODE_PRIVATE
            )
        }

        single {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        single {
            var builder = OkHttpClient.Builder().apply {
                connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                // protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))

                connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
                retryOnConnectionFailure(true)
                interceptors().add(get<HeaderInterceptor>())
                // interceptors().add(get<ChuckInterceptor>())
                // authenticator(get<TokenAuthenticator>())

                if (BaseApplication.getAppContext().isDebug()) {
                    interceptors().add(get<HttpLoggingInterceptor>())
                }
            }
            builder.build()
        }



        single { SharedHelper(get(), get()) }
//        single { VerseNoteSpanHelper(com.oyetech.materialViews.R.drawable.ic_verse_note_img) }
        singleOf(::SharedPrefRepositoryImp)
        single<SharedHelperRepository> { SharedPrefRepositoryImp(get()) }
//        singleOf(::ClipboardOperationHelper)
        singleOf(::TokenAuthenticator)
        singleOf(::RefreshTokenHelper)
        singleOf(::ForceUpdateHelper)
        // singleOf(::ChuckInterceptor)
        // singleOf(::SignalRHelper)

        single<SharedOperationRepository> { SharedPrefRepository(get()) }

        singleOf(::HeaderInterceptor)
        singleOf(::AuthOperationBodyHelper)
        singleOf(::UnreadMessageCalculatorHelper)
        singleOf(::OnlineOfflineStatusHelper)
        singleOf(::LanguageHelper)

        /*
        single<AnalyticsRepository> { AnalyticsRepositoryImp(get()) }
        single<GoogleSubscriptionOperationRepository> { GoogleSubscriptionOperationImp(get(), get(), get()) }



         */
    }

}
