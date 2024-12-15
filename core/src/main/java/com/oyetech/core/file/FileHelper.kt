package com.oyetech.core.file

import android.content.Context
import android.media.MediaMetadataRetriever
import android.util.Log
import com.oyetech.models.entity.file.audio.AudioMessageResponseData
import com.oyetech.models.entity.file.audio.AudioMessageResponseSubData
import com.oyetech.models.entity.file.audio.mapToNormalize
import com.oyetech.models.entity.messages.MessageDetailDataResponse
import com.oyetech.models.utils.const.HelperConstant
import com.oyetech.models.utils.moshi.deserialize
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
Created by Erdi Özbek
-14.04.2022-
-17:48-
 **/

object FileHelper {

    fun listProjectDirFiles(path: String) {
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files: Array<File> = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files", "FileName:" + files[i].getName())
        }
    }

    fun saveFileToPath(inputStream: InputStream?, filePath: String): String {
        if (inputStream == null) return ""
        var input: InputStream? = null
        try {
            input = inputStream
            val fos = FileOutputStream(filePath)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                    Timber.d("write...")
                }
                output.flush()
            }
            return filePath
        } catch (e: Exception) {
            Timber.d("exception == " + e.toString())
            e.printStackTrace()
        } finally {
            input?.close()
        }
        return ""
    }

    fun createFileNameForAudioCacheFile(context: Context, fileNameId: String): String {
        return context.filesDir.absolutePath + "/nacFiles/" + fileNameId + ".m4a"
    }

    fun createFileDirForAudioFile(context: Context): String {
        return context.filesDir.absolutePath + "/nacFiles/"
    }

    fun getAudioFileFromMessageDetail(messageDetailDataResponse: MessageDetailDataResponse): File? {
        var audioMessageResponseData: AudioMessageResponseData? = null
        try {
            audioMessageResponseData =
                messageDetailDataResponse.content.deserialize<AudioMessageResponseSubData>()
                    ?.mapToNormalize()
        } catch (e: Exception) {
            Timber.d("deserilize null = " + e.printStackTrace())
            return null
        }
        if (audioMessageResponseData == null) {
            Timber.d("data null = ")
            return null
        }

        var filePathString = audioMessageResponseData.audioFilePath
        var uploadFile = File(filePathString)
        return uploadFile
    }

    fun calculateTime(file: File): Long {
        try {
            val metaRetriever = MediaMetadataRetriever()
            metaRetriever.setDataSource(file.absolutePath)
            val duration =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            if (duration == null) {
                return 0L
            }
            val durationLong = duration!!.toLong()
            Timber.d("file timeee = " + durationLong)
            if (durationLong < HelperConstant.AUDIO_RECORD_MIN_MILLISECOND) {
                return 0L
            }
            metaRetriever.release()
            return durationLong
        } catch (e: Exception) {
            e.printStackTrace()
            return 0L
        }
    }

    fun readJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }


}
