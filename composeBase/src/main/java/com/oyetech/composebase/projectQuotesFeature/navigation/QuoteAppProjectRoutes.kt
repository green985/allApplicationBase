package com.oyetech.composebase.projectQuotesFeature.navigation

import com.oyetech.composebase.R
import com.oyetech.composebase.baseViews.bottomNavigation.BottomNavigationItem
import com.oyetech.composebase.navigator.Route
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.QuoteSettingsTab
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppNavItem.TabQuoteAppHomepage
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.QuoteAppHomepage
import com.oyetech.composebase.projectQuotesFeature.navigation.QuoteAppProjectRoutes.QuoteSettings
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-6.01.2025-
-23:25-
 **/

object QuoteAppProjectRoutes {

    val QuoteAdviceScreen = Route("quoteAdviceScreen")
    val QuoteAppHomepage = Route("quoteAppHomepage")
    val QuoteDetailRoute = Route("quoteDetail")
    val QuoteAuthorList = Route("quoteAuthorList")
    val QuoteSettings = Route("quoteSettings")
    val AdviceQuoteDebug = Route("adviceQuoteDebug")
    val ContactScreen = Route("quote/ContactScreen")
    val CompleteProfileScreen = Route("LoginOperationScreen/CompleteProfileScreen")
    val LoginOperationScreen = Route("LoginOperationScreen")

    val quoteApplicationBottomTabNavList = listOf(
        TabQuoteAppHomepage, QuoteSettingsTab
    )
}

sealed class QuoteAppNavItem {
    object TabQuoteAppHomepage :
        BottomNavigationItem(
            path = QuoteAppHomepage.route.toString(),
            titleText = LanguageKey.home,
            icon = R.drawable.ic_tab_all_radio
        )

    object QuoteSettingsTab :
        BottomNavigationItem(
            path = QuoteSettings.route.toString(),
            title = R.string.nav_item_settings,
            icon = R.drawable.ic_settings
        )

}
