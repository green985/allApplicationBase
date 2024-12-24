package com.oyetech.firebaseDB.firebaseDB.comment

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.oyetech.domain.repository.firebase.FirebaseCommentOperationRepository
import com.oyetech.firebaseDB.databaseKeys.FirebaseDatabaseKeys
import com.oyetech.models.firebaseModels.commentModel.CommentData
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

/**
Created by Erdi Özbek
-20.12.2024-
-22:13-
 **/

class FirebaseCommentOperationRepositoryImp(private val firestore: FirebaseFirestore) :
    FirebaseCommentOperationRepository {

    override fun getCommentsWithId(commentId: String) {
        firestore.collection(FirebaseDatabaseKeys.commentTable)
            .document(commentId)
            .collection("comments")
//            .orderBy("createdAtFieldValue")
            .get()
            .addOnSuccessListener { result ->
                result.documents.forEach {
                    val commentModel = it.toObject(CommentData::class.java)
                    flowOf(commentModel)
                    Timber.d("it ... " + it.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting comments", e)
            }
    }

    override fun addComment(contentId: String, content: String) {
        firestore.collection(FirebaseDatabaseKeys.commentTable)
            .document(contentId)
            .collection("comments")
            .add(
                CommentData(
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
