package com.oyetech.models.utils.states

import androidx.annotation.IntDef

/**
Created by Erdi Özbek
-23.03.2022-
-18:03-
 **/

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@IntDef(SENDING, SENT, DELIVERED, SEEN, ERROR, RECEIVED)
@Retention(AnnotationRetention.SOURCE)
annotation class MessagesState

const val SENDING = 0
const val SENT = 1
const val DELIVERED = 2
const val SEEN = 3
const val RECEIVED = 4
const val ERROR = 400

@IntDef(AUDIO_IDLE, AUDIO_PLAYING, AUDIO_BUFFERING, AUDIO_STOP, AUDIO_ERROR)
@Retention(AnnotationRetention.SOURCE)
annotation class AudioState

const val AUDIO_IDLE = 67
const val AUDIO_PLAYING = 15
const val AUDIO_BUFFERING = 24
const val AUDIO_STOP = 32
const val AUDIO_ERROR = 4001
