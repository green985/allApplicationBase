package com.oyetech.kmpfeatures.navigation

import androidx.navigation.compose.composable
import com.oyetech.kmpdomain.usecase.navigation.NavigationUseCase
import com.oyetech.kmpfeatures.daily.DailyPagerScreenSetup
import com.oyetech.kmpfeatures.diary.DiaryScreenSetup
import com.oyetech.kmpfeatures.example.OperatorExampleScreenSetup
import com.oyetech.kmpfeatures.home.HomeScreenSetup
import com.oyetech.kmpfeatures.login.LoginScreenSetup
import com.oyetech.kmpmodels.navigation.RouteKMP
import org.koin.compose.koinInject

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
) {
    composable(KmpFeaturesRoutes.path(RouteKMP.Home)) {
        HomeScreenSetup()
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Login)) {
        val navigationUseCase = koinInject<NavigationUseCase>()
        LoginScreenSetup(
            onBackClick = navigationUseCase::goBack,
            onLoginSuccess = {
                navigationUseCase.navigateTo(RouteKMP.Diary)
            },
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.DailyPager)) {
        val navigationUseCase = koinInject<NavigationUseCase>()
        DailyPagerScreenSetup(
            onBackClick = navigationUseCase::goBack,
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Diary)) {
        val navigationUseCase = koinInject<NavigationUseCase>()
        DiaryScreenSetup(
            onBackClick = navigationUseCase::goBack,
        )
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.OperatorExample)) {
        OperatorExampleScreenSetup()
    }
}
