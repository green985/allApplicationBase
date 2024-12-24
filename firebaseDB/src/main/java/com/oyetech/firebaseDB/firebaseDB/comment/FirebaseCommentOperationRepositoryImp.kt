package com.oyetech.firebaseDB.firebaseDB.comment

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.commentModel.CommentResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import timber.log.Timber

/**
Created by Erdi Özbek
-20.12.2024-
-22:13-
 **/

class FirebaseCommentOperationRepositoryImp(private val firestore: FirebaseFirestore) :
    FirebaseCommentOperationRepository {

    override suspend fun getCommentsWithId(commentId: String): Flow<List<CommentResponseData>> {

        val result = firestore.collection(FirebaseDatabaseKeys.commentTable)
            .document(commentId)
            .collection("comments")
            .get().await()

        if (result.size() == 0) {
            Timber.d("No comments found")
            return flowOf(emptyList())
        }

        val wrapperResult = result.toObjects(CommentResponseData::class.java)
        return flowOf(wrapperResult)

    }

    override fun addComment(contentId: String, content: String) {
        firestore.collection(FirebaseDatabaseKeys.commentTable)
            .document(contentId)
            .collection("comments")
            .add(
                CommentResponseData(
                    content = content,
                )
            )
            .addOnSuccessListener {
                Timber.d("Comment added")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding comment", e)
            }
    }
}
