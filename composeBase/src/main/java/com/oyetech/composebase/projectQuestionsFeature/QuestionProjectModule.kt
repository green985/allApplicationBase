package com.oyetech.composebase.projectQuestionsFeature

import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.main.QuestionMainActivityVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionPagerVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases.GetQuestionsPagedByCreatedAtUseCase
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.usecases.GetUserQuestionsPagedByCreatedAtUseCase
import com.oyetech.composebase.sharedScreens.settings.FacSettingsVm
import com.oyetech.composebase.sharedScreens.userProfile.userProfileDesign.UserProfileVm2
import com.oyetech.composebase.sharedViews.floating.FloatingAskQuestionBarVm
import com.oyetech.domain.useCases.QuestionUseCase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object QuestionProjectModule {
    val module = module {

        singleOf(::QuestionUseCase)
        singleOf(::GetUserQuestionsPagedByCreatedAtUseCase)
        factoryOf(::GetQuestionsPagedByCreatedAtUseCase)
        viewModelOf(::QuestionMainActivityVm)
        viewModelOf(::QuestionCreateQuestionVm)
        viewModelOf(::FacSettingsVm)

        viewModelOf(::QuestionListVm)
        viewModelOf(::QuestionPagerVm)
        viewModelOf(::AdminApproveQuestionVm)
        viewModelOf(::FloatingAskQuestionBarVm)
        viewModelOf(::UserProfileVm2)
    }
}
