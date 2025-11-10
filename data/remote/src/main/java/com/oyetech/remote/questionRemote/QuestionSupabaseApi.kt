package com.oyetech.remote.questionRemote

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuestionSupabaseApi {

    @POST("v1/question-post")
    suspend fun createQuestion(
        @Body question: QuestionOperationResponseBody,
    ): Response<QuestionOperationResponseBody>
}
