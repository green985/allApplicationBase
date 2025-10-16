package com.oyetech.domain.repository.firebase

import com.oyetech.models.questionProject.questionOperation.QueTag
import kotlinx.coroutines.flow.Flow

interface FirebaseQuestionTagRepository {
    fun addTagToQuestion(questionId: String, tag: QueTag): Flow<Unit>
    fun removeTagFromQuestion(questionId: String, tagId: String): Flow<Unit>
    fun getQuestionTags(questionId: String): Flow<List<QueTag>>
}
