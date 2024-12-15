package com.oyetech.domain.repository

import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseData
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.InputStream

/**
Created by Erdi Özbek
-20.04.2022-
-01:49-
 **/

interface FileOperationRepository {

    fun onUploadImageFile(uploadImage: File, toUserId: Long): Flow<ImageMessageResponseData>

    fun onUploadAudioFile(
        uploadAudioFile: File,
        toUserId: Long,
        duration: Long,
    ): Flow<AudioMessageResponseData>

    suspend fun downloadAudioWithUrl(downloadAudioUrl: String): InputStream?
}
