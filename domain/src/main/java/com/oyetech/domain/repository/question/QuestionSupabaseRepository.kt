package com.oyetech.domain.repository.question

import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.GenerateFormResultResponse
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionFormDetailResponse
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import com.oyetech.models.questionProject.questionOperation.SubmitCatalogResponse
import kotlinx.coroutines.flow.Flow

interface QuestionSupabaseRepository {
    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody>
    fun updateQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody>
    fun updateQuestionStatus(request: QuestionStatusUpdateRequest): Flow<QuestionOperationResponseBody>
    fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<UserProfileProperty>
    fun updateUser(userProfileProperty: UserProfileProperty): Flow<UserProfileProperty>
    fun getUserWithToken(getUserWithTokenBody: GetUserWithTokenBody): Flow<UserProfileProperty>
    fun getQuestionListWithFilterParam(filterParam: QueFilter): Flow<QuestionListWithFilterResponse>
    fun addAnswer(answer: QueAnswer): Flow<QueAnswer>
    fun getAnswersByUser(userId: String): Flow<List<QueAnswer>>
    suspend fun getAnswersByQuestion(questionId: String): Flow<List<QueAnswer>>
    fun updateAnswer(answer: QueAnswer): Flow<QueAnswer>
    fun deleteAnswer(userId: String, questionId: String): Flow<Boolean>
    fun getCatalogDetail(formId: String, userId: String): Flow<QuestionFormDetailResponse>
    fun submitCatalog(
        formId: String,
        userId: String,
        questions: List<QuestionOperationResponseBody>,
    ): Flow<SubmitCatalogResponse>

    fun generateFormResult(
        formId: String,
        userId: String,
        prompt: String,
        notificationToken: String?,
    ): Flow<GenerateFormResultResponse>
}
