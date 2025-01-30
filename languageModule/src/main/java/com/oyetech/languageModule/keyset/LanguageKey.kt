@file:Suppress("UNUSED_PARAMETER")

package com.oyetech.languageModule.keyset

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper

/**
Created by Erdi Özbek
-22.12.2024-
-20:08-
 **/

object LanguageKey {
    var adviceQuote set(value) {} get() = LocalLanguageHelper.getStringWithKey("adviceQuote")
    var commentLoginButtonText set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentLoginButtonText")
    var usernameInfoText set(value) {} get() = LocalLanguageHelper.getStringWithKey("usernameInfoText")
    var deleteAccountSuccess set(value) {} get() = LocalLanguageHelper.getStringWithKey("deleteAccountSuccess")
    var deleteDialogInfo set(value) {} get() = LocalLanguageHelper.getStringWithKey("deleteDialogInfo")
    var settings set(value) {} get() = LocalLanguageHelper.getStringWithKey("settings")
    var home set(value) {} get() = LocalLanguageHelper.getStringWithKey("home")
    var errorText set(value) {} get() = LocalLanguageHelper.getStringWithKey("errorText")
    var report set(value) {} get() = LocalLanguageHelper.getStringWithKey("report")
    var delete set(value) {} get() = LocalLanguageHelper.getStringWithKey("delete")
    var emptyCommentError set(value) {} get() = LocalLanguageHelper.getStringWithKey("emptyCommentError")
    var commentAddedSuccessfully set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentAddedSuccessfully")
    var commentInputAreaHint set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentInputAreaHint")
    var commentCannotBeTooLong set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentCannotBeTooLong")
    var commentCannotBeTooShort set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentCannotBeTooShort")
    var commentCannotBeEmpty set(value) {} get() = LocalLanguageHelper.getStringWithKey("commentCannotBeEmpty")
    var quotes set(value) {} get() = LocalLanguageHelper.getStringWithKey("quotes")
    var usernameIsEmpty set(value) {} get() = LocalLanguageHelper.getStringWithKey("usernameIsEmpty")
    var createUserErrorMessage set(value) {} get() = LocalLanguageHelper.getStringWithKey("createUserErrorMessage")
    var accountDeleted set(value) {} get() = LocalLanguageHelper.getStringWithKey("accountDeleted")
    var deleteAccountButtonText set(value) {} get() = LocalLanguageHelper.getStringWithKey("deleteAccountButtonText")
    var deleteUserErrorMessage set(value) {} get() = LocalLanguageHelper.getStringWithKey("deleteAccountButtonText")

    var generalErrorText set(value) {} get() = LocalLanguageHelper.getStringWithKey("Error")
    var internetConnectionErrorText set(value) {} get() = LocalLanguageHelper.getStringWithKey("internetConnectionErrorText")
    var login set(value) {} get() = LocalLanguageHelper.getStringWithKey("Login")
    var denemeKey set(value) {} get() = LocalLanguageHelper.getStringWithKey("denemeKey")

}