package com.oyetech.exoplayermodule.helper

import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.oyetech.domain.useCases.contentOperations.RadioOperationUseCase
import com.oyetech.exoplayermodule.errorHandling.CustomLoadErrorHandlingPolicy
import org.koin.java.KoinJavaComponent

/**
Created by Erdi Özbek
-19.11.2022-
-00:48-
 **/

abstract class ExoPlayerDiHelper {

    val defaultDataSource: DefaultDataSource.Factory by KoinJavaComponent.inject(
        DefaultDataSource.Factory::class.java
    )

    val radioOperationUseCase: RadioOperationUseCase by KoinJavaComponent.inject(
        RadioOperationUseCase::class.java
    )

    val hlsMediaSource: HlsMediaSource.Factory by KoinJavaComponent.inject(
        HlsMediaSource.Factory::class.java
    )

    val progressiveMediaSource: ProgressiveMediaSource.Factory by KoinJavaComponent.inject(
        ProgressiveMediaSource.Factory::class.java
    )

    fun getCustomErrorHandlingPolicyy(): CustomLoadErrorHandlingPolicy {
        return CustomLoadErrorHandlingPolicy(radioOperationUseCase)
    }
}