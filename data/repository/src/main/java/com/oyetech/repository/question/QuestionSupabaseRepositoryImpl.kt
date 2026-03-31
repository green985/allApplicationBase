package com.oyetech.repository.question

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.DeleteAccountResponse
import com.oyetech.models.questionProject.questionOperation.GetCatalogListResponse
import com.oyetech.models.questionProject.questionOperation.QueAnswer
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionFormDetailResponse
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.models.questionProject.questionOperation.QuestionStatusUpdateRequest
import com.oyetech.models.questionProject.questionOperation.SubmitCatalogResponse
import com.oyetech.remote.questionRemote.QuestionSupabaseDataSource
import kotlinx.coroutines.flow.Flow

class QuestionSupabaseRepositoryImpl(
    private val questionSupabaseDataSource: QuestionSupabaseDataSource,
) : QuestionSupabaseRepository {

    // todo will be change return type...
    override fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return questionSupabaseDataSource.createQuestion(question)
    }

    override fun updateQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return questionSupabaseDataSource.updateQuestion(question)
    }

    override fun updateQuestionStatus(request: QuestionStatusUpdateRequest): Flow<QuestionOperationResponseBody> {
        return questionSupabaseDataSource.updateQuestionStatus(request)
    }

    override fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<UserProfileProperty> {
        return questionSupabaseDataSource.registerGoogleUser(googleUserResponseData)
    }

    override fun updateUser(userProfileProperty: UserProfileProperty): Flow<UserProfileProperty> {
        return questionSupabaseDataSource.updateUser(userProfileProperty)
    }

    override fun getUserWithToken(getUserWithTokenBody: GetUserWithTokenBody): Flow<UserProfileProperty> {
        return questionSupabaseDataSource.getUserWithToken(getUserWithTokenBody)
    }

    override fun getQuestionListWithFilterParam(
        filterParam: QueFilter,
    ): Flow<QuestionListWithFilterResponse> {
        return questionSupabaseDataSource.getQuestionListWithFilterParam(filterParam)
    }

    override fun addAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return questionSupabaseDataSource.addAnswer(answer)
    }

    override fun getAnswersByUser(userId: String): Flow<List<QueAnswer>> {
        return questionSupabaseDataSource.getAnswersByUser(userId)
    }

    override suspend fun getAnswersByQuestion(questionId: String): Flow<List<QueAnswer>> {
        return questionSupabaseDataSource.getAnswersByQuestion(questionId)
    }

    override fun updateAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return questionSupabaseDataSource.updateAnswer(answer)
    }

    override fun deleteAnswer(userId: String, questionId: String): Flow<Boolean> {
        return questionSupabaseDataSource.deleteAnswer(userId, questionId)
    }

    override fun deleteAccount(): Flow<DeleteAccountResponse> {
        return questionSupabaseDataSource.deleteAccount()
    }

    override fun getCatalogDetail(
        formId: String,
        userId: String,
    ): Flow<QuestionFormDetailResponse> {
        return questionSupabaseDataSource.getCatalogDetail(formId, userId)
    }

    override fun submitCatalog(
        formId: String,
        userId: String,
        questions: List<QuestionOperationResponseBody>,
    ): Flow<SubmitCatalogResponse> {
        return questionSupabaseDataSource.submitCatalog(formId, userId, questions)
    }

    override fun generateFormResult(
        formId: String,
        userId: String,
        prompt: String,
        notificationToken: String?,
        token: String?,
    ): Flow<SubmitCatalogResponse> {
        return questionSupabaseDataSource.generateFormResult(
            formId,
            userId,
            prompt,
            notificationToken,
            token
        )
    }

    override fun getCatalogList(queryText: String): Flow<GetCatalogListResponse> {
        return questionSupabaseDataSource.getCatalogList(queryText)
    }
}
