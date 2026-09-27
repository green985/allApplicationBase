package com.oyetech.kmpfeatures.network

import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

val KMPHeaderInterceptor = createClientPlugin("KMPHeaderInterceptor") {
    onRequest { request, _ ->
        request.headers.append(HttpHeaders.ContentType, "application/json")

        request.headers.remove(HttpHeaders.Authorization)
        request.headers.append(
            HttpHeaders.Authorization,
            "Bearer ${KMPAuthConfig.anonToken}",
        )
        request.headers.remove("apikey")
        request.headers.append("apikey", KMPAuthConfig.anonToken)
    }
}
