package com.oyetech.remote.questionRemote

import com.oyetech.models.GenericResponse
import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.DeleteAccountResponse
import com.oyetech.models.questionProject.questionOperation.DeleteAnswerRequest
import com.oyetech.models.questionProject.questionOperation.GenerateFormResultRequest
import com.oyetech.models.questionProject.questionOperation.GenerateFormResultResponse
import com.oyetech.models.questionProject.questionOperation.GetCatalogListRequest
import com.oyetech.models.questionProject.questionOperation.GetCatalogListResponse
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionFormDetailRequest
import com.oyetech.models.questionProject.questionOperation.QuestionFormDetailResponse
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import com.oyetech.models.questionProject.questionOperation.SubmitCatalogRequest
import com.oyetech.models.questionProject.questionOperation.SubmitCatalogResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @GET("v1/getAnswersByUser")
    suspend fun getAnswersByUser(
        @Query("userId") userId: String,
    ): Response<GenericResponse<List<QueAnswer>>>

    @GET("v1/getAnswersByQuestion")
    suspend fun getAnswersByQuestion(
        @Query("questionId") questionId: String,
    ): Response<GenericResponse<List<QueAnswer>>>

    @POST("v1/updateAnswer")
    suspend fun updateAnswer(
        @Body answer: QueAnswer,
    ): Response<GenericResponse<QueAnswer>>

    @POST("v1/deleteAnswer")
    suspend fun deleteAnswer(
        @Body request: DeleteAnswerRequest,
    ): Response<GenericResponse<Boolean>>

    @DELETE("v1/deleteUser")
    suspend fun deleteAccount(): Response<GenericResponse<DeleteAccountResponse>>

    @POST("v1/getCatalogDetail")
    suspend fun getCatalogDetail(
        @Body request: QuestionFormDetailRequest,
    ): Response<GenericResponse<QuestionFormDetailResponse>>

    @POST("v1/submitCatalog")
    suspend fun submitCatalog(
        @Body request: SubmitCatalogRequest,
    ): Response<GenericResponse<SubmitCatalogResponse>>

    @POST("v1/generateFormResult")
    suspend fun generateFormResult(
        @Body request: GenerateFormResultRequest,
    ): Response<GenericResponse<GenerateFormResultResponse>>

    @POST("v1/getCatalogList")
    suspend fun getCatalogList(
        @Body request: GetCatalogListRequest,
    ): Response<GenericResponse<GetCatalogListResponse>>
}
