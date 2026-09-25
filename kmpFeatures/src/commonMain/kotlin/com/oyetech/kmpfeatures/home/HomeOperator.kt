package com.oyetech.kmpfeatures.home

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.kmpmodels.stringKeys.StringKeys
import com.oyetech.kmpmodels.ui.event.HomeAction
import com.oyetech.kmpmodels.ui.state.HomeUiState
import kotlinx.coroutines.CoroutineScope

class HomeOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
) : BaseFeatureOperator<HomeUiState, HomeAction, Nothing>(
    initialState = HomeUiState(
        welcomeMessage = StringKeys.kmpFeaturesReady,
    ),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.AdminLoginClicked -> {
                navigationUseCase.navigateTo(RouteKMP.Diary)
            }

            HomeAction.OperatorExampleClicked -> {
                navigationUseCase.navigateTo(RouteKMP.OperatorExample)
            }

            HomeAction.ErrorDismissed -> Unit
        }
    }
}
