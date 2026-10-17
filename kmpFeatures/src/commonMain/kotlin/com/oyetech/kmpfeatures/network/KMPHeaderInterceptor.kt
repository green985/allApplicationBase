package com.oyetech.kmpfeatures.network

import com.oyetech.kmpfeatures.auth.GoogleLoginOperation
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import kotlinx.coroutines.CancellationException

fun KMPHeaderInterceptor(sessionOperation: () -> GoogleLoginOperation) =
    createClientPlugin("KMPHeaderInterceptor") {
        on(Send) { request ->
            val url = request.url.build()
            if (url.host != "uduwhuvgdcacvdhzheyi.supabase.co" ||
                url.protocol.name != "https" || url.port != 443
            ) {
                return@on proceed(request)
            }
            val operation = sessionOperation()
            request.headers[HttpHeaders.ContentType] = "application/json"
            request.headers["apikey"] = KMPAuthConfig.anonToken
            if (!operation.supportsSessions) {
                request.headers[HttpHeaders.Authorization] = "Bearer ${KMPAuthConfig.anonToken}"
                if (request.url.encodedPath.startsWith("/functions/v1/kmpFunctions/")) {
                    request.headers["X-KMP-Access-Mode"] = "admin"
                }
                return@on proceed(request)
            }
            val sessionKey = operation.sessionKey
            val user = operation.currentUser()
            if (operation.sessionKey != sessionKey) throw CancellationException("Session changed")
            request.headers.remove(HttpHeaders.Authorization)
            request.headers.remove("X-KMP-Access-Mode")
            if (user.isAdmin) {
                request.headers["X-KMP-Access-Mode"] = "admin"
            } else {
                request.headers[HttpHeaders.Authorization] = "Bearer ${user.accessToken}"
            }
            val call = proceed(request)
            if (operation.sessionKey != sessionKey) {
                call.response.bodyAsChannel().cancel(null)
                throw CancellationException("Session changed")
            }
            if (call.response.status != HttpStatusCode.Unauthorized || user.isAdmin) return@on call
            call.response.bodyAsChannel().cancel(null)
            if (operation.sessionKey != sessionKey) throw CancellationException("Session changed")
            val refreshedUser = operation.currentUser(rejectedToken = user.accessToken)
            if (operation.sessionKey != sessionKey) throw CancellationException("Session changed")
            request.headers[HttpHeaders.Authorization] = "Bearer ${refreshedUser.accessToken}"
            val retry = proceed(request)
            if (operation.sessionKey != sessionKey) {
                retry.response.bodyAsChannel().cancel(null)
                throw CancellationException("Session changed")
            }
            if (retry.response.status == HttpStatusCode.Unauthorized) {
                operation.expireSession(rejectedToken = refreshedUser.accessToken)
            }
            retry
        }
    }
