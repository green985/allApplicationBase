package com.oyetech.domain.useCases

import com.oyetech.domain.repository.VolumeHelperRepository

/**
Created by Erdi Özbek
-17.12.2022-
-02:05-
 **/

class VolumeOperationUseCase(private var repository: VolumeHelperRepository) {
    fun volumeUp() {
        repository.volumeUp()
    }

    fun volumeDown() {
        repository.volumeDown()
    }

    fun volumeMaxWithDelay(delay: Long) {
        repository.volumeMaxWithDelay(delay)
    }

}