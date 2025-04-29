package com.oyetech.radioservice.broadcasts

import android.content.BroadcastReceiver
import com.oyetech.domain.repository.SharedOperationRepository
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-22.11.2022-
-00:56-
 **/

abstract class BaseBroadcastReceiver : BroadcastReceiver() {

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )

    val sharedOperationUseCase: SharedOperationRepository by KoinJavaComponent.inject(
        SharedOperationRepository::class.java
    )


}