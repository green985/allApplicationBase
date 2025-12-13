package com.oyetech.remote.questionRemote

import com.oyetech.models.entity.GenericResponse
import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuestionSupabaseApi {

    @POST("v1/questionPost")
    suspend fun createQuestion(
        @Body question: QuestionOperationResponseBody,
    ): Response<GenericResponse<QuestionOperationResponseBody>>

    @POST("v1/questionUpdate")
    suspend fun updateQuestion(
        @Body question: QuestionOperationResponseBody,
    ): Response<GenericResponse<QuestionOperationResponseBody>>

    @POST("v1/questionUpdate")
    suspend fun updateQuestionStatus(
        @Body request: QuestionStatusUpdateRequest,
    ): Response<GenericResponse<QuestionOperationResponseBody>>

    @POST("v1/registerGoogleUser")
    suspend fun registerGoogleUser(
        @Body body: GoogleUserPostData,
    ): Response<GenericResponse<UserProfileProperty>>

    @POST("v1/updateUserProfile")
    suspend fun updateUser(
        @Body body: UserProfileProperty,
    ): Response<GenericResponse<UserProfileProperty>>

    @POST("v1/getUserWithToken")
    suspend fun getUserWithToken(
        @Body body: GetUserWithTokenBody,
    ): Response<GenericResponse<UserProfileProperty>>

    @POST("v1/getQuestionListWithFilterParam")
    suspend fun getQuestionListWithFilterParam(
        @Body filterParam: QueFilter,
    ): Response<GenericResponse<QuestionListWithFilterResponse>>

    @POST("v1/addAnswer")
    suspend fun addAnswer(
        @Body answer: QueAnswer,
    ): Response<GenericResponse<QueAnswer>>
}
