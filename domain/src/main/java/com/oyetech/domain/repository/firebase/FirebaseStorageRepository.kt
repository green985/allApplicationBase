package com.oyetech.domain.repository.firebase

import android.net.Uri
import com.oyetech.models.firebaseModels.UploadResult
import kotlinx.coroutines.flow.Flow

interface FirebaseStorageRepository {
    fun uploadImage(uri: Uri): Flow<UploadResult>
}
