package com.oyetech.kmpfeatures.navigation

import androidx.navigation.compose.composable
import com.oyetech.kmpfeatures.daily.DailyPagerScreenSetup
import com.oyetech.kmpfeatures.diary.DiaryScreenSetup
import com.oyetech.kmpfeatures.example.OperatorExampleScreenSetup
import com.oyetech.kmpfeatures.home.HomeScreenSetup
import com.oyetech.kmpfeatures.login.LoginScreenSetup
import com.oyetech.kmpmodels.navigation.RouteKMP

fun androidx.navigation.NavGraphBuilder.kmpFeaturesNavGraph(
) {
    composable(KmpFeaturesRoutes.path(RouteKMP.Home)) {
        HomeScreenSetup()
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Login)) {
        LoginScreenSetup()
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.DailyPager)) {
        DailyPagerScreenSetup()
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.Diary)) {
        DiaryScreenSetup()
    }
    composable(KmpFeaturesRoutes.path(RouteKMP.OperatorExample)) {
        OperatorExampleScreenSetup()
    }
}
