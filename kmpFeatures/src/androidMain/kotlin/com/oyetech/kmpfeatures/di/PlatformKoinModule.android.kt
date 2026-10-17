package com.oyetech.kmpfeatures.di

import android.media.AudioManager
import android.media.ToneGenerator
import com.oyetech.kmpfeatures.example.OperatorExampleOperator
import com.oyetech.kmpfeatures.example.OperatorExampleViewModel
import kotlinx.coroutines.delay
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

actual val platformKoinModule = module {
    viewModel {
        OperatorExampleViewModel(
            operatorFactory = { operatorScope ->
                get<OperatorExampleOperator> {
                    parametersOf(operatorScope)
                }
            },
        )
    }
}

internal actual suspend fun playTimerFinishedSound() {
    val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 50)
    try {
        check(tone.startTone(ToneGenerator.TONE_DTMF_5, 3000)) {
            "Timer notification sound could not be started"
        }
        delay(3050)
    } finally {
        tone.release()
    }
}
