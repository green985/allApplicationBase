package com.oyetech.helper.audio

import android.content.Context
import android.media.MediaRecorder
import com.oyetech.core.file.FileHelper.createFileDirForAudioFile
import org.koin.java.KoinJavaComponent
import timber.log.Timber
import java.io.File
import java.io.IOException

/**
Created by Erdi Özbek
-24.03.2022-
-00:17-
 **/

class RecordAudioHelper {

    val context: Context by KoinJavaComponent.inject<Context>(Context::class.java)

    init {
        Timber.d("Record Audio Helper init")
    }

    private var state: Boolean = false
    var audiofile: File? = null

    var mediaRecorder = MediaRecorder()

    var lastRecordedTempId: String = ""

    fun initMediaRecorderProperty() {
        createFile()
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)

        mediaRecorder.setAudioChannels(2)
        mediaRecorder.setAudioSamplingRate(44100)
        mediaRecorder.setAudioEncodingBitRate(96000)

        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

        mediaRecorder?.setOutputFile(audiofile?.absolutePath)
    }

    fun getRecordingAudioFile(): File? {
        return audiofile
    }

    fun createFile() {
        var fileName = createFileDirForAudioFile(context)
        Timber.d("audioCreatedFileName = " + fileName)
        val dir = File(fileName)
        dir.mkdir()
        // val dir: File = Environment.getExternalStorageDirectory()
        var fileSuffix = lastRecordedTempId
        Timber.d("audioCreatedFileName = " + fileSuffix)
        try {
            audiofile = File.createTempFile(fileSuffix, ".m4a", dir)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
    }

    fun startRecording(tempId: String) {
        try {
            this.lastRecordedTempId = tempId
            initMediaRecorderProperty()
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
            Timber.d("record starting")
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        if (state) {
            // mediaRecorder?.stop()
            // mediaRecorder?.release()
            mediaRecorder.reset()
            state = false
        } else {
            Timber.d("\"You are not recording right now!\"")
        }
    }
}
