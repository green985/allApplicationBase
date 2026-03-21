package com.oyetech.remote.firebaseCloudRemote

import com.oyetech.models.firebaseModels.cloudFunction.FirebaseCloudNotificationBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
Created by Erdi Özbek
-18.04.2025-
-13:25-
 **/

interface FirebaseCloudApi {

    @POST("/sendNotificationWithPayloadWithDateChange")
    suspend fun sendNotificationWithPayloadWithDateChange(@Body body: FirebaseCloudNotificationBody): Response<Boolean>

    @POST("/sendNotificationWithPayload")
    suspend fun sendNotificationWithPayload(@Body body: FirebaseCloudNotificationBody): Response<Boolean>
}
