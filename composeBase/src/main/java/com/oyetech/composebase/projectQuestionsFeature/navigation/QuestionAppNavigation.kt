package com.oyetech.composebase.projectQuestionsFeature.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.oyetech.composebase.experimental.loginOperations.CompleteProfileScreenSetup
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.homeScreen.QuestionsHomeScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListWithParamsScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionPagerScreenSetup
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.QuestionFormScreenSetup
import com.oyetech.composebase.sharedScreens.settings.FacSettingsScreenSetup
import com.oyetech.composebase.sharedScreens.userList.UserListScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.editProfile.EditUserProfileScreenSetup
import com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign.User2ProfileScreenSetup

@Suppress("LongMethod")
fun EntryProviderScope<NavKey>.questionAppNavigation() {

    entry<AppRoute.QuestionAppHomepage> {
        QuestionsHomeScreenSetup()
    }

    entry<AppRoute.QuestionAppSettings> {
        FacSettingsScreenSetup()
    }

    entry<AppRoute.MessageConversationList> {
        // MessageConversationListScreenSetup()
    }

    entry<AppRoute.UserList> {
        UserListScreenSetup()
    }

    entry<AppRoute.QuestionCreateQuestionPage> {
        QuestionCreateScreenSetup(questionId = it.questionId)
    }

    entry<AppRoute.AdminApproveQuestion> {
        AdminApproveQuestionScreenSetup()
    }

    entry<AppRoute.QuestionPager> {
        QuestionPagerScreenSetup()
    }

    entry<AppRoute.EditProfile> {
        EditUserProfileScreenSetup()
    }

    entry<AppRoute.QuestionListWithParams> {
        QuestionListWithParamsScreenSetup(
            questionTagId = it.questionTag,
            adminFilterTypeStr = it.adminFilterType
        )
    }

    entry<AppRoute.QuestionFormScreen> {
        QuestionFormScreenSetup(formId = it.formId)
    }

    entry<AppRoute.CompleteProfileScreen> {
        CompleteProfileScreenSetup()
    }

    entry<AppRoute.UserProfile> {
        User2ProfileScreenSetup(receiverUserId = it.receiverUserId)
    }

    entry<AppRoute.MessageDetail> {
        // MessageDetailScreenSetup(receiverUserId = it.receiverUserId)
    }
}
