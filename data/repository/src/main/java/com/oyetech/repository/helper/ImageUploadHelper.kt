package com.oyetech.repository.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.webkit.MimeTypeMap
import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.imageFile.ImageMessageResponseData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
Created by Erdi Özbek
-7.04.2022-
-02:00-
 **/

fun bitmatCompres(image: File): File {
    var newImageFile = image
    if (!newImageFile.extension.equals("gif")) {
        try {
            val imagePath: String = image.getAbsolutePath() // photoFile is a File class.

            val myBitmap = BitmapFactory.decodeFile(imagePath)

            val orientedBitmap = ExifUtil.rotateBitmap(imagePath, myBitmap)
            val bos = ByteArrayOutputStream()
            orientedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)

            newImageFile = File(image.path)
            newImageFile.createNewFile()
            val fos = FileOutputStream(newImageFile)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return newImageFile
}

fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(toString())
        ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase()) }
        ?: fallback // You might set it to */*
}

suspend fun createCountingRequestForImageMessageBody(
    requestBody: RequestBody,
    emitter: ProducerScope<ImageMessageResponseData>,
): RequestBody {
    return CountingRequestBody(
        requestBody,
        CountingRequestBody.Listener { bytesWritten: Long, contentLength: Long ->
            val progress = 1.0 * bytesWritten / contentLength
            Timber.d("progressss = " + progress)
            try {
                GlobalScope.launch(emitter.coroutineContext) {
                    var model = ImageMessageResponseData()
                    model.progressStatus = progress
                    emitter.send(model)
                }
            } catch (e: Exception) {
                Timber.d("exception ")
                e.printStackTrace()
            }
        }
    )
}

suspend fun createCountingRequestForAudioMessageBody(
    requestBody: RequestBody,
    emitter: ProducerScope<AudioMessageResponseData>,
): RequestBody {
    return CountingRequestBody(
        requestBody,
        CountingRequestBody.Listener { bytesWritten: Long, contentLength: Long ->
            val progress = 1.0 * bytesWritten / contentLength
            Timber.d("progressss = " + progress)
            try {
                GlobalScope.launch(emitter.coroutineContext) {
                    var model = AudioMessageResponseData()
                    model.progressStatus = progress
                    emitter.send(model)
                }
            } catch (e: Exception) {
                Timber.d("exception ")
                e.printStackTrace()
            }
        }
    )
}
