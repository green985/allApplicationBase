package com.oyetech.composebase.projectQuotesFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oyetech.composebase.projectQuotesFeature.authorListScreen.AuthorListScreen
import com.oyetech.composebase.projectQuotesFeature.homeScreen.QuotesHomeScreenSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.navigateRoute
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.sharedScreens.quotes.detail.QuoteDetailScreenSetup

@Suppress("LongMethod")
fun NavGraphBuilder.quotesAppNavigation(navController: NavController) {
    composable(QuoteAppProjectRoutes.QuoteHomeRoute.route) {
        QuotesHomeScreenSetup()
    }
    composable(
        QuoteAppProjectRoutes.QuoteDetailRoute.route, arguments = listOf(
            navArgument(ScreenKey.quoteId) {
                defaultValue = "randomSingle"
                nullable = false
            },
        )
    ) {
        val quoteId = it.arguments?.getString(ScreenKey.quoteId) ?: "randomSingle"
        QuoteDetailScreenSetup(
            quoteId = quoteId,
            navigationRoute = navigateRoute(navController)
        )
    }
    composable(QuoteAppProjectRoutes.QuoteAuthorList.route) {
        AuthorListScreen(navigationRoute = navigateRoute(navController))
    }
}