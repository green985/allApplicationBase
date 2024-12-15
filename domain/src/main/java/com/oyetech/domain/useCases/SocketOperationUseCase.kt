package com.oyetech.domain.useCases

import com.oyetech.domain.repository.socketOperation.SignalrOperationRepository
import timber.log.Timber

/**
Created by Erdi Özbek
-10.03.2022-
-17:08-
 **/

class SocketOperationUseCase(
    private var repository: SignalrOperationRepository
) {

    fun closeConnectionWithActivityLifecycle() {

        repository.closeConnectionWithActivityLifecycle()
    }

    fun startConnection() {

        repository.startConnection()
    }

    fun deathConnection() {
        repository.deathConnection()
    }

    fun getIsSocketConnect() = repository.isSocketConnect

    fun onMessageReadEvent(messageReadRequestBodyString: String) {
        Timber.d("onMessageReadEvent called..." + messageReadRequestBodyString)
        repository.sendClientReadMessage(messageReadRequestBodyString)
    }

    fun sendTypingEvent(userId: Long) {
        repository.sendTypingEvent(userId)
    }

    fun sendRecordingVoiceEvent(userId: Long) {
        repository.sendRecordingVoiceEvent(userId)
    }
}
