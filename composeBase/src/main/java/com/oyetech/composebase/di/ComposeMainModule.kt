package com.oyetech.composebase.di

import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationDelegate
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationVm
import com.oyetech.composebase.baseViews.snackbar.SnackbarDelegate
import com.oyetech.composebase.experimental.commentWidget.CommentScreenWithContentIdVM
import com.oyetech.composebase.experimental.loginOperations.LoginOperationVM
import com.oyetech.composebase.experimental.moonOperation.MoonOperationVm
import com.oyetech.composebase.helpers.adViewDelegate.AdViewOperationDelegate
import com.oyetech.composebase.helpers.adViewDelegate.AdViewOperationDelegateImpl
import com.oyetech.composebase.helpers.eventNavigator.TestEventNavigator
import com.oyetech.composebase.helpers.vibrationHelper.IVibrationHelper
import com.oyetech.composebase.helpers.vibrationHelper.VibrationHelperImpl
import com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.GeneralOperationVM
import com.oyetech.composebase.projectQuestionsFeature.generalOperationScreen.generalPlayground.GeneralPlaygroundVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.QuestionFormViewModel
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.questionForm.questionFormList.QuestionFormListViewModel
import com.oyetech.composebase.sharedScreens.allScreenNavigator.AllScreenNavigatorVM
import com.oyetech.composebase.sharedScreens.messaging.MessageDetailVm
import com.oyetech.composebase.sharedScreens.messaging.MessageOperationVM
import com.oyetech.composebase.sharedScreens.messaging.conversationList.MessageConversationListVm
import com.oyetech.composebase.sharedScreens.userList.UserListVm
import com.oyetech.composebase.sharedScreens.userProfile.editProfile.EditProfileVm
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
Created by Erdi Özbek
-30.09.2024-
-23:15-
 **/

object ComposeMainModule {
    val composeMainModule1 = module {
        singleOf(::TestEventNavigator)
        single<IVibrationHelper> { VibrationHelperImpl(get()) }
        factory<AdViewOperationDelegate> { AdViewOperationDelegateImpl() }
        // pruned radio/quote VMs after package removal
        single<SnackbarDelegate> { SnackbarDelegate() }
        single<BottomNavigationDelegate> { BottomNavigationDelegate() }


        singleOf(::GeneralOperationVM)
        singleOf(::GeneralPlaygroundVm)
        singleOf(::LoginOperationVM)

        viewModelOf(::CommentScreenWithContentIdVM)

        // messaging
        viewModelOf(::MessageDetailVm)
        viewModelOf(::QuestionFormViewModel)
        viewModelOf(::QuestionFormListViewModel)
        singleOf(::MessageOperationVM)
        viewModelOf(::MessageConversationListVm)
        viewModelOf(::UserListVm)
        viewModelOf(::AllScreenNavigatorVM)
        viewModelOf(::MoonOperationVm)
        viewModelOf(::BottomNavigationVm)
        viewModelOf(::EditProfileVm)
    }
}
