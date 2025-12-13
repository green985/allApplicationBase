package com.oyetech.domain.repository.question

import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionListWithFilterResponse
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import kotlinx.coroutines.flow.Flow

interface QuestionSupabaseRepository {
    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody>
    fun updateQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody>
    fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<UserProfileProperty>
    fun updateUser(userProfileProperty: UserProfileProperty): Flow<UserProfileProperty>
    fun getUserWithToken(getUserWithTokenBody: GetUserWithTokenBody): Flow<UserProfileProperty>
    fun getQuestionListWithFilterParam(filterParam: QueFilter): Flow<QuestionListWithFilterResponse>
}
