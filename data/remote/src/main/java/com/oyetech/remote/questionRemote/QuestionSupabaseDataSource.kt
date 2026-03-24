package com.oyetech.remote.questionRemote

import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.DeleteAccountRequest
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
import com.oyetech.remote.helper.interceptGenericResponseTrueForm
import kotlinx.coroutines.flow.Flow

class QuestionSupabaseDataSource(private val questionSupabaseApi: QuestionSupabaseApi) {

    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.createQuestion(question)
        }
    }

    fun updateQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateQuestion(question)
        }
    }

    fun updateQuestionStatus(request: QuestionStatusUpdateRequest): Flow<QuestionOperationResponseBody> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateQuestionStatus(request)
        }
    }

    fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.registerGoogleUser(googleUserResponseData)
        }
    }

    fun updateUser(userProfileProperty: UserProfileProperty): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateUser(userProfileProperty)
        }
    }

    fun getUserWithToken(getUserWithTokenBody: GetUserWithTokenBody): Flow<UserProfileProperty> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.getUserWithToken(getUserWithTokenBody)
        }
    }

    fun getQuestionListWithFilterParam(filterParam: QueFilter): Flow<QuestionListWithFilterResponse> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.getQuestionListWithFilterParam(filterParam)
        }
    }

    fun addAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.addAnswer(answer)
        }
    }

    fun getAnswersByUser(userId: String): Flow<List<QueAnswer>> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.getAnswersByUser(userId)
        }
    }

    suspend fun getAnswersByQuestion(questionId: String): Flow<List<QueAnswer>> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.getAnswersByQuestion(questionId)
        }
    }

    fun updateAnswer(answer: QueAnswer): Flow<QueAnswer> {
        return interceptGenericResponseTrueForm {
            questionSupabaseApi.updateAnswer(answer)
        }
    }

    fun deleteAnswer(userId: String, questionId: String): Flow<Boolean> {
        return interceptGenericResponseTrueForm {
            val request = DeleteAnswerRequest(userId = userId, questionId = questionId)
            questionSupabaseApi.deleteAnswer(request)
        }
    }

    fun deleteAccount(userId: String): Flow<Boolean> {
        return interceptGenericResponseTrueForm {
            val request = DeleteAccountRequest(userId = userId)
            questionSupabaseApi.deleteAccount(request)
        }
    }

    fun getCatalogDetail(formId: String, userId: String): Flow<QuestionFormDetailResponse> {
        return interceptGenericResponseTrueForm {
            val request = QuestionFormDetailRequest(formId = formId, userId = userId)
            questionSupabaseApi.getCatalogDetail(request)
        }
    }

    fun submitCatalog(
        formId: String,
        userId: String,
        questions: List<QuestionOperationResponseBody>,
    ): Flow<SubmitCatalogResponse> {
        return interceptGenericResponseTrueForm {
            val request =
                SubmitCatalogRequest(formId = formId, userId = userId, questions = questions)
            questionSupabaseApi.submitCatalog(request)
        }
    }

    fun generateFormResult(
        formId: String,
        userId: String,
        prompt: String,
        notificationToken: String?,
        token: String?,
    ): Flow<GenerateFormResultResponse> {
        return interceptGenericResponseTrueForm {
            val request = GenerateFormResultRequest(
                formId = formId,
                userId = userId,
                prompt = prompt,
                notificationToken = notificationToken,
                token = token,
            )
            questionSupabaseApi.generateFormResult(request)
        }
    }

    fun getCatalogList(queryText: String): Flow<GetCatalogListResponse> {
        return interceptGenericResponseTrueForm {
            val request = GetCatalogListRequest(queryText = queryText)
            questionSupabaseApi.getCatalogList(request)
        }
    }
}
