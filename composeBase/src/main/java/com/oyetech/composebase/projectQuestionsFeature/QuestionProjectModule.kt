package com.oyetech.composebase.projectQuestionsFeature

import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.events.QuestionUpdateEventBus
import com.oyetech.composebase.projectQuestionsFeature.main.QuestionMainActivityVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListVm
import com.oyetech.composebase.sharedScreens.settings.FacSettingsVm
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object QuestionProjectModule {
    val module = module {
        singleOf(::QuestionUpdateEventBus)
        viewModelOf(::QuestionMainActivityVm)
        viewModelOf(::QuestionCreateQuestionVm)
        viewModelOf(::AdminApproveQuestionVm)
        viewModelOf(::FacSettingsVm)
        viewModelOf(::QuestionListVm)
    }
}
