package com.oyetech.domain.useCases

import android.net.Uri
import androidx.core.net.toFile
import com.oyetech.domain.repository.FileOperationRepository
import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.InputStream

/**
Created by Erdi Özbek
-20.04.2022-
-02:00-
 **/

class FileOperationUseCase(private var fileOperationRepository: FileOperationRepository) {

    fun onUploadImageFile(
        imagesList: ArrayList<Uri?>,
        toUserId: Long,
    ): Flow<ImageMessageResponseData> {
        return flow<ImageMessageResponseData> {
            imagesList.map {
                if (it != null) {
                    val imageFile = it.toFile()
                    emitAll(fileOperationRepository.onUploadImageFile(imageFile, toUserId))
                }
            }
        }
    }

    fun onUploadImageFile(
        imageFile: File,
        toUserId: Long,
    ): Flow<ImageMessageResponseData> {
        return flow<ImageMessageResponseData> {
            emitAll(fileOperationRepository.onUploadImageFile(imageFile, toUserId))
        }
    }

    fun onUploadAudioFile(
        audioFile: File,
        toUserId: Long,
        duration: Long,
    ): Flow<AudioMessageResponseData> {
        return fileOperationRepository.onUploadAudioFile(audioFile, toUserId, duration)
    }

    suspend fun downloadAudioWithUrl(downloadAudioUrl: String): InputStream? {
        return fileOperationRepository.downloadAudioWithUrl(downloadAudioUrl)
    }
}
