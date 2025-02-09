package com.oyetech.composebase.helpers.general

import com.oyetech.composebase.BuildConfig

/**
Created by Erdi Özbek
-18.01.2025-
-11:19-
 **/

object GeneralSettings {

    fun isDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    fun isCommentSectionEnable(): Boolean {
        return false
    }

    fun isAddQuoteSectionEnable(): Boolean {
        return false
    }

    fun isLoginOperationEnable(): Boolean {
        return false
    }

    fun isAutoOperationEnable(): Boolean {
        return false
    }

    fun isRatingEnable(): Boolean {

        return false
    }

}