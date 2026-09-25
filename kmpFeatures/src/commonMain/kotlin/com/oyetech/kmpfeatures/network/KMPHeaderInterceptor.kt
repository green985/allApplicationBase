package com.oyetech.kmpfeatures.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

val KMPHeaderInterceptor = createClientPlugin("KMPHeaderInterceptor") {
    onRequest { request, _ ->
        request.headers.remove(HttpHeaders.Authorization)
        request.headers.append(HttpHeaders.Authorization, "Bearer dummy-auth-token")
    }
}
