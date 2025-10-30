package com.oyetech.composebase.helpers.general

import com.oyetech.domain.BuildConfig

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
        return true
    }

    fun isAddQuoteSectionEnable(): Boolean {
        return false
    }

    fun isRealTimeOperationEnable(): Boolean {
        return false
    }

    fun isLoginOperationEnable(): Boolean {
        return true
    }

    fun isAutoOperationEnable(): Boolean {
        return false
    }

    fun isRatingEnable(): Boolean {

        return false
    }

    fun isViewVisible(): Boolean {
        return true
    }

    fun isAdmin(): Boolean {
        return isDebug()
    }

}
