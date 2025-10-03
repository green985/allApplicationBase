package com.oyetech.composebase.projectQuestionsFeature

import com.oyetech.composebase.projectQuestionsFeature.adminApprove.AdminApproveQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.createQuestion.QuestionCreateQuestionVm
import com.oyetech.composebase.projectQuestionsFeature.questionScreens.list.QuestionListVm
import com.oyetech.composebase.sharedScreens.settings.FacSettingsVm
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

object QuestionProjectModule {
    val module = module {
        viewModelOf(::QuestionCreateQuestionVm)
        viewModelOf(::AdminApproveQuestionVm)
        viewModelOf(::FacSettingsVm)
        viewModelOf(::QuestionListVm)
    }
}
