package com.oyetech.firebaseDB.files

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.oyetech.domain.repository.firebase.FirebaseStorageRepository
import com.oyetech.models.firebaseModels.UploadResult
import com.oyetech.models.firebaseModels.UploadResult.Progress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

class FirebaseStorageRepositoryImpl(
    private val storage: FirebaseStorage,
) : FirebaseStorageRepository {

    override fun uploadImage(uri: Uri): Flow<UploadResult> = callbackFlow {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageRef = storage.reference.child("images/$fileName")
        val uploadTask = storageRef.putFile(uri)

        val progressListener = OnProgressListener<TaskSnapshot> { snapshot ->
            val progress = ((100.0 * snapshot.bytesTransferred) / snapshot.totalByteCount).toInt()
            trySend(Progress(progress))
        }

        val successListener = OnSuccessListener<TaskSnapshot> {
            storageRef.downloadUrl
                .addOnSuccessListener { uri ->
                    trySend(UploadResult.Success(uri.toString()))
                    close()
                }
                .addOnFailureListener { exception ->
                    trySend(UploadResult.Error(exception))
                    close()
                }
        }

        val failureListener = OnFailureListener { exception ->
            trySend(UploadResult.Error(exception))
            close()
        }

        uploadTask
            .addOnProgressListener(progressListener)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)

        awaitClose {
            uploadTask.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
