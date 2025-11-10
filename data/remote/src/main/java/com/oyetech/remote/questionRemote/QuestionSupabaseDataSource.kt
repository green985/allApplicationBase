package com.oyetech.remote.questionRemote

import com.oyetech.models.questionProject.questionOperation.QuestionOperationResponseBody
import com.oyetech.remote.helper.interceptTrueForm
import kotlinx.coroutines.flow.Flow

class QuestionSupabaseDataSource(private val questionSupabaseApi: QuestionSupabaseApi) {

    fun createQuestion(question: QuestionOperationResponseBody): Flow<QuestionOperationResponseBody> {
        return interceptTrueForm {
            questionSupabaseApi.createQuestion(question)
        }
    }
}
