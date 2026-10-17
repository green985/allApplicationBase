package com.oyetech.kmpfeatures.di

import com.oyetech.kmpdomain.delegate.snackbar.SnackbarDelegate
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.auth.GoogleIdentityProvider
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.daily.DailyPagerOperator
import com.oyetech.kmpfeatures.diary.DiaryEndpointOperation
import com.oyetech.kmpfeatures.diary.DiaryOperator
import com.oyetech.kmpfeatures.example.OperatorExampleOperator
import com.oyetech.kmpfeatures.home.HomeOperator
import com.oyetech.kmpfeatures.login.LoginOperator
import com.oyetech.kmpfeatures.network.KMPHeaderInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KmpFeaturesKoin {
    val module: Module = module {
        singleOf(::NavigationUseCase)
        singleOf(::SnackbarDelegate)
        singleOf(::GoogleIdentityProvider)
        singleOf(::GoogleLoginOperation)
        single {
            val sessionOperation = { get<GoogleLoginOperation>() }
            HttpClient {
                install(KMPHeaderInterceptor(sessionOperation))
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
        singleOf(::DiaryEndpointOperation)
        factory { parameters ->
            LoginOperator(
                operatorScope = parameters.get<CoroutineScope>(),
                googleLoginOperation = get(),
                navigationUseCase = get(),
            )
        }
        factory { parameters ->
            DailyPagerOperator(
                operatorScope = parameters.get<CoroutineScope>(),
                navigationUseCase = get(),
            )
        }
        factory { parameters ->
            DiaryOperator(
                operatorScope = parameters.get<CoroutineScope>(),
                navigationUseCase = get(),
                diaryEndpointOperation = get(),
                snackbarDelegate = get(),
            )
        }
        factory { parameters ->
            HomeOperator(
                operatorScope = parameters.get<CoroutineScope>(),
                navigationUseCase = get(),
                googleLoginOperation = get(),
            )
        }
        factory { parameters ->
            OperatorExampleOperator(
                operatorScope = parameters.get<CoroutineScope>(),
                navigationUseCase = get(),
                snackbarDelegate = get(),
            )
        }
    }
}
