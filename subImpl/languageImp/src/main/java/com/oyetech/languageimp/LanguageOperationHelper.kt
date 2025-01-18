package com.oyetech.languageimp

import com.oyetech.domain.repository.SharedOperationRepository
import timber.log.Timber

/**
Created by Erdi Özbek
-18.01.2025-
-17:32-
 **/

class LanguageOperationHelper(private val sharedOperationRepository: SharedOperationRepository) {

    fun initLanguageData() {
        Timber.d("Language Data Initialized")
    }


}