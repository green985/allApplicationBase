package com.oyetech.languageModule.keyset

import com.oyetech.languageModule.localLanguageHelper.LocalLanguageHelper

/**
Created by Erdi Özbek
-22.12.2024-
-20:08-
 **/

object LanguageKey {
    // QuestionYesNoView keys
    var questionAddedSuccessfullyText set(value) {} get() = LocalLanguageHelper.getStringWithKey("questionAddedSuccessfullyText")
    var answerYes set(value) {} get() = LocalLanguageHelper.getStringWithKey("answerYes")
    var answerNo set(value) {} get() = LocalLanguageHelper.getStringWithKey("answerNo")
    var untitledQuestionText set(value) {} get() = LocalLanguageHelper.getStringWithKey("untitledQuestionText")
    var yesText set(value) {} get() = LocalLanguageHelper.getStringWithKey("YES")
    var noText set(value) {} get() = LocalLanguageHelper.getStringWithKey("NO")
    var yourAnswerText set(value) {} get() = LocalLanguageHelper.getStringWithKey("yourAnswerText")

    var recordingStatusOff set(value) {} get() = LocalLanguageHelper.getStringWithKey("recordingStatusOff")
    var voiceRecorderTitle set(value) {} get() = LocalLanguageHelper.getStringWithKey("voiceRecorderTitle")
    var startRecording set(value) {} get() = LocalLanguageHelper.getStringWithKey("startRecording")
    var stopRecording set(value) {} get() = LocalLanguageHelper.getStringWithKey("stopRecording")
    var recordingStatusOn set(value) {} get() = LocalLanguageHelper.getStringWithKey("recordingStatusOn")
    var unknownUserText set(value) {} get() = LocalLanguageHelper.getStringWithKey("unknownUserText")
    var userFeedListTitle set(value) {} get() = LocalLanguageHelper.getStringWithKey("userFeedListTitle")
    var messageDetailTitle set(value) {} get() = LocalLanguageHelper.getStringWithKey("messageDetailTitle")
    var messageConversationTitle set(value) {} get() = LocalLanguageHelper.getStringWithKey("messageConversationTitle")
    var userNotFound set(value) {} get() = LocalLanguageHelper.getStringWithKey("userNotFound")
    var userProfileNotFound set(value) {} get() = LocalLanguageHelper.getStringWithKey("userProfileNotFound")
    var userIdNotFound set(value) {} get() = LocalLanguageHelper.getStringWithKey("userIdNotFound")
    var messageListErrorUserNotFound set(value) {} get() = LocalLanguageHelper.getStringWithKey("messageListErrorUserNotFound")
    var sendMessageHint set(value) {} get() = LocalLanguageHelper.getStringWithKey("sendMessageHint")
    var profileCreatedAt set(value) {} get() = LocalLanguageHelper.getStringWithKey("profileCreatedAt")
    var biographyInfoHint set(value) {} get() = LocalLanguageHelper.getStringWithKey("biographyInfoHint")
    var save set(value) {} get() = LocalLanguageHelper.getStringWithKey("save")
    var cancel set(value) {} get() = LocalLanguageHelper.getStringWithKey("cancel")
    var dismiss set(value) {} get() = LocalLanguageHelper.getStringWithKey("dismiss")
    var termsAndConditionText set(value) {} get() = LocalLanguageHelper.getStringWithKey("termsAndConditionText")
    var privacyPolicyText set(value) {} get() = LocalLanguageHelper.getStringWithKey("privacyPolicyText")
    var applicationInfoText set(value) {} get() = LocalLanguageHelper.getStringWithKey("applicationInfoText")
    var userListFeedEmptyError set(value) {} get() = LocalLanguageHelper.getStringWithKey("userListFeedEmptyError")
    var connectWithPeople set(value) {} get() = LocalLanguageHelper.getStringWithKey("connectWithPeople")
    var ageCannotBeNull set(value) {} get() = LocalLanguageHelper.getStringWithKey("ageCannotBeNull")
    var invalidAgeError set(value) {} get() = LocalLanguageHelper.getStringWithKey("invalidAgeError")
    var genderCannotBeEmpty set(value) {} get() = LocalLanguageHelper.getStringWithKey("genderCannotBeEmpty")
    var messageSendHint set(value) {} get() = LocalLanguageHelper.getStringWithKey("messageSendHint")
    var conversationNotFound set(value) {} get() = LocalLanguageHelper.getStringWithKey("conversationNotFound")

    // added to firebase 12.02.2025 for general language keys
    var copyToClipboardSuccess set(value) {} get() = LocalLanguageHelper.getStringWithKey("copyToClipboardSuccess")
    var rateUs set(value) {} get() = LocalLanguageHelper.getStringWithKey("rateUs")
    var contactWithUs set(value) {} get() = LocalLanguageHelper.getStringWithKey("contactWithUs")
    var contactWithUsSentSuccessfully
        set(value) {}
        get() =
            LocalLanguageHelper.getStringWithKey("contactWithUsSentSuccessfully")
    var quoteSentSuccessfully set(value) {} get() = LocalLanguageHelper.getStringWithKey("Quote sent successfully")
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

    // Migrations from WallpaperLanguage
    var feedbackAlreadySend set(value) {} get() = LocalLanguageHelper.getStringWithKey("FEEDBACK_ALREADY_SEND")
    var googleSignInError set(value) {} get() = LocalLanguageHelper.getStringWithKey("GOOGLE_SIGN_IN_ERROR")
    var downloadImageDesc set(value) {} get() = LocalLanguageHelper.getStringWithKey("DOWNLOAD_IMAGE_DESC")
    var downloadImageStart set(value) {} get() = LocalLanguageHelper.getStringWithKey("DOWNLOAD_IMAGE_START")
    var writeExternalRequired set(value) {} get() = LocalLanguageHelper.getStringWithKey("WRITE_EXTERNAL_REQUIRED")
    var adFreeUse set(value) {} get() = LocalLanguageHelper.getStringWithKey("AD_FREE_USE")
    var termsAndConditions set(value) {} get() = LocalLanguageHelper.getStringWithKey("TERMS_AND_CONDITIONS")
    var privacyPolicy set(value) {} get() = LocalLanguageHelper.getStringWithKey("PRIVACY_POLICY")
    var appName set(value) {} get() = LocalLanguageHelper.getStringWithKey("Thought Nest")
    var internetConnectionErrorText
        set(value) {}
        get() =
            LocalLanguageHelper.getStringWithKey("internetConnectionErrorText")
    var login set(value) {} get() = LocalLanguageHelper.getStringWithKey("Login")
    var accept set(value) {} get() = LocalLanguageHelper.getStringWithKey("accept")
    var decline set(value) {} get() = LocalLanguageHelper.getStringWithKey("decline")

    // Admin question filter tabs
    var all set(value) {} get() = LocalLanguageHelper.getStringWithKey("all")
    var pending set(value) {} get() = LocalLanguageHelper.getStringWithKey("pending")
    var approved set(value) {} get() = LocalLanguageHelper.getStringWithKey("approved")
    var declined set(value) {} get() = LocalLanguageHelper.getStringWithKey("declined")
    var approveAll set(value) {} get() = LocalLanguageHelper.getStringWithKey("approveAll")
    var declineAll set(value) {} get() = LocalLanguageHelper.getStringWithKey("declineAll")
}
