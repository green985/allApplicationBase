package com.oyetech.repository.question

import com.oyetech.domain.repository.question.QuestionSupabaseRepository
import com.oyetech.models.firebaseModels.googleAuth.GoogleUserPostData
import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.remote.questionRemote.QuestionSupabaseDataSource
import kotlinx.coroutines.flow.Flow

class QuestionSupabaseRepositoryImpl(
    private val questionSupabaseDataSource: QuestionSupabaseDataSource,
) : QuestionSupabaseRepository {

    override fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return questionSupabaseDataSource.createQuestion(question)
    }

    override fun registerGoogleUser(googleUserResponseData: GoogleUserPostData): Flow<QuestionOperationResponseBody> {
        return questionSupabaseDataSource.registerGoogleUser(googleUserResponseData)
    }
}
