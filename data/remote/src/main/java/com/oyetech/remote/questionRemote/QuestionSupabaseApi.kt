package com.oyetech.remote.questionRemote

import com.oyetech.models.entity.GenericResponse
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuestionSupabaseApi {

    @POST("v1/question-post")
    suspend fun createQuestion(
        @Body question: QuestionOperationResponseBody,
    ): Response<QuestionOperationResponseBody>


    @POST("v1/registerGoogleUser")
    suspend fun registerGoogleUser(
        @Body body: GoogleUserPostData,
    ): Response<GenericResponse<UserProfileProperty>>

    @POST("v1/updateUserProfile")
    suspend fun updateUser(
        @Body body: UserProfileProperty,
    ): Response<GenericResponse<UserProfileProperty>>
}
