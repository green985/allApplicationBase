package com.oyetech.composebase.projectQuotesFeature.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oyetech.composebase.projectQuotesFeature.adviceQuote.AdviceQuoteScreenSetup
import com.oyetech.composebase.projectQuotesFeature.authorListScreen.AuthorListScreen
import com.oyetech.composebase.projectQuotesFeature.homeScreen.QuotesHomeScreenSetup
import com.oyetech.composebase.projectQuotesFeature.quoteSettingsScreen.QuoteSettingsScreenSetup
import com.oyetech.composebase.projectQuotesFeature.quotes.detail.QuoteDetailScreenSetup
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.navigateRouteOperation
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey

@Suppress("LongMethod")
fun NavGraphBuilder.quotesAppNavigation(navController: NavController) {
    composable(QuoteAppProjectRoutes.QuoteAppHomepage.route) {
        QuotesHomeScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }
    composable(
        route = "${QuoteAppProjectRoutes.QuoteDetailRoute.route}?" +
                "${ScreenKey.quoteId}={quoteId}", arguments = listOf(
            navArgument(ScreenKey.quoteId) {
                defaultValue = "randomSingle"
                nullable = false
            },
        )
    ) {
        val quoteId = it.arguments?.getString(ScreenKey.quoteId) ?: "randomSingle"
        QuoteDetailScreenSetup(
            quoteId = quoteId,
            navigationRoute = navigateRouteOperation(navController)
        )
    }
    composable(QuoteAppProjectRoutes.QuoteAuthorList.route) {
        AuthorListScreen(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.QuoteAdviceScreen.route) {
        AdviceQuoteScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }

    composable(QuoteAppProjectRoutes.QuoteSettings.route) {
        QuoteSettingsScreenSetup(navigationRoute = navigateRouteOperation(navController))
    }
}