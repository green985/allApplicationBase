package com.oyetech.kmpfeatures.di

import com.oyetech.kmpfeatures.auth.GoogleIdentityProvider
import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import com.oyetech.kmpfeatures.example.KmpFeaturesExampleOperation
import com.oyetech.kmpfeatures.login.LoginViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object KmpFeaturesKoin {
    val module: Module = module {
        single {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
        singleOf(::GoogleIdentityProvider)
        singleOf(::GoogleLoginOperation)
        singleOf(::LoginViewModel)
        singleOf(::KmpFeaturesExampleOperation)
    }
}
