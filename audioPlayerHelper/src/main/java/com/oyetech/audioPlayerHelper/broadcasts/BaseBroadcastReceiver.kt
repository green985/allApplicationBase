package com.oyetech.audioPlayerHelper.broadcasts

import android.content.BroadcastReceiver
import com.oyetech.domain.useCases.SharedOperationUseCase
import com.oyetech.domain.useCases.contentOperations.ContentOperationUseCase
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-22.11.2022-
-00:56-
 **/

abstract class BaseBroadcastReceiver : BroadcastReceiver() {

    val contentOperationUseCase: ContentOperationUseCase by KoinJavaComponent.inject(
        ContentOperationUseCase::class.java
    )

    val sharedOperationUseCase: SharedOperationUseCase by KoinJavaComponent.inject(
        SharedOperationUseCase::class.java
    )


}