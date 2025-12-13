package com.oyetech.repository.question

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.firebaseModels.googleAuth.GetUserWithTokenBody
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.firebaseModels.userModel.UserProfileProperty
import com.oyetech.models.questionProject.questionOperation.QueFilter
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
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
    ): Flow<List<QuestionOperationResponseBody>> {
        return questionSupabaseDataSource.getQuestionListWithFilterParam(filterParam)
    }
}
