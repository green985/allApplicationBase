package com.oyetech.firebaseDB.firebaseDB.question

import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseQuestionTagRepository
import com.oyetech.models.questionProject.questionOperation.QueTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class FirebaseQuestionTagRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseQuestionTagRepository {

    override fun addTagToQuestion(questionId: String, tag: QueTag): Flow<Unit> = flow {
        Timber.d("addTagToQuestion called: questionId=$questionId, tag=${tag.id}")
        // TODO: Implementation will be added later
        emit(Unit)
    }

    override fun removeTagFromQuestion(questionId: String, tagId: String): Flow<Unit> = flow {
        Timber.d("removeTagFromQuestion called: questionId=$questionId, tagId=$tagId")
        // TODO: Implementation will be added later
        emit(Unit)
    }

    override fun getQuestionTags(questionId: String): Flow<List<QueTag>> = flow {
        Timber.d("getQuestionTags called: questionId=$questionId")
        // TODO: Implementation will be added later
        emit(emptyList())
    }
}
