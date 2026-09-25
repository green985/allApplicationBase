package com.oyetech.kmpfeatures.home

import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.example.KmpFeaturesExampleOperation
import com.oyetech.kmpfeatures.login.LoginViewModel
import com.oyetech.kmpfeatures.operator.BaseFeatureOperator
import com.oyetech.kmpmodels.navigation.RouteKMP
import com.oyetech.kmpmodels.ui.event.HomeAction
import com.oyetech.kmpmodels.ui.state.HomeUiState
import kotlinx.coroutines.CoroutineScope

class HomeOperator(
    operatorScope: CoroutineScope,
    private val navigationUseCase: NavigationUseCase,
    private val loginViewModel: LoginViewModel,
    private val exampleOperation: KmpFeaturesExampleOperation,
) : BaseFeatureOperator<HomeUiState, HomeAction, Nothing>(
    initialState = HomeUiState(
        welcomeMessage = exampleOperation.getWelcomeMessage(),
    ),
    operatorScope = operatorScope,
) {
    override fun handleAction(action: HomeAction) {
        when (action) {
            HomeAction.AdminLoginClicked -> {
                loginViewModel.loginAsLocalAdmin()
                navigationUseCase.navigateTo(RouteKMP.Diary)
            }

            HomeAction.OperatorExampleClicked -> {
                navigationUseCase.navigateTo(RouteKMP.OperatorExample)
            }

            HomeAction.ErrorDismissed -> Unit
        }
    }
}
