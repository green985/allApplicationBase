package com.oyetech.composebase.projectRadioFeature.navigationRoutes

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.oyetech.composebase.experimental.commentScreen.CommentScreenSetup
import com.oyetech.composebase.experimental.loginOperations.CompleteProfileScreenSetup
import com.oyetech.composebase.experimental.loginOperations.LoginOperationScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.radioSearchList.RadioSearchListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.tabAllList.TabAllListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.tabSettings.contactWithMe.ContactScreen
import com.oyetech.composebase.projectRadioFeature.screens.tagList.TagListScreenSetup
import com.oyetech.composebase.projectRadioFeature.screens.views.dialogs.timerDialog.RadioCountTimerDialogSetup
import com.oyetech.models.radioProject.enums.RadioListEnums
import timber.log.Timber

@Suppress("LongMethod")
fun NavGraphBuilder.radioAppNavigation(navController: NavController) {

    // TabRadioAllList Route
    composable(RadioAppProjectRoutes.TabRadioAllList.route) {
        TabAllListScreenSetup(navigateRoute(navController))
    }
    // TabRadioAllList Route
    composable(RadioAppProjectRoutes.ContactScreen.route) {
        ContactScreen(navigationRoute = navigateRoute(navController))
    }
    composable(RadioAppProjectRoutes.RadioSearchList.route) {
        RadioSearchListScreenSetup(navigationRoute = navigateRoute(navController))
    }

    // TimerDialog Route
    dialog(RadioAppProjectRoutes.TimerDialog.route) {
        RadioCountTimerDialogSetup(navController = navController)
    }

    // TabFav Route
    composable(RadioAppProjectRoutes.TabFav.route) {
        RadioListScreenSetup(
            listType = RadioListEnums.Favorites.name,
            navigationRoute = navigateRoute(navController)
        )
    }
    // TabFav Route
    composable(RadioAppProjectRoutes.TabCategories.route) {
        TagListScreenSetup(navigationRoute = navigateRoute(navController))
    }

    // TabHistory Route
    composable(RadioAppProjectRoutes.TabHistory.route) {
        RadioListScreenSetup(
            listType = RadioListEnums.History.name,
            navigationRoute = navigateRoute(navController)
        )
    }

    // TabHistory Route
    composable(RadioAppProjectRoutes.TabSettings.route) {
        LoginOperationScreenSetup(
            navigationRoute = navigateRoute(navController)
        )

//        TabSettingsScreenSetup(
//            navigationRoute = navigateRoute(navController)
//        )
    }

    // TabHistory Route
    composable(RadioAppProjectRoutes.QuotesListScreen.route) {
        CommentScreenSetup(
//            navigationRoute = navigateRoute(navController)
        )
    }

    // TabHistory Route
    composable(RadioAppProjectRoutes.CommentScreen.route) {
        CommentScreenSetup(
//            navigationRoute = navigateRoute(navController)
        )
    }
    // TabHistory Route
    composable(RadioAppProjectRoutes.LoginOperationScreen.route) {
        LoginOperationScreenSetup(
            navigationRoute = navigateRoute(navController)
        )
    }
    // TabHistory Route
    composable(RadioAppProjectRoutes.CompleteProfileScreen.route) {
        CompleteProfileScreenSetup(
            navigationRoute = navigateRoute(navController)
        )
    }

    // RadioList Route with Arguments
    composable(
        route = "${RadioAppProjectRoutes.RadioList.route}?" +
                "${ScreenKey.listType}={listType}" +
                "&${ScreenKey.languageName}={languageName}" +
                "&${ScreenKey.tagName}={tagName}" +
                "&${ScreenKey.countryName}={countryName}" +
                "&${ScreenKey.toolbarTitle}={toolbarTitle}",
        arguments = listOf(
            navArgument(ScreenKey.listType) {
                defaultValue = RadioListEnums.Languages.name
                nullable = false
            },
            navArgument(ScreenKey.tagName) { nullable = true },
            navArgument(ScreenKey.languageName) { nullable = true },
            navArgument(ScreenKey.countryName) { nullable = true }
        )
    ) {
        val listType = it.arguments?.getString(ScreenKey.listType)
        val tagName = it.arguments?.getString(ScreenKey.tagName)
        val languageName = it.arguments?.getString(ScreenKey.languageName)
        val countryName = it.arguments?.getString(ScreenKey.countryName)
        val toolbarTitle = it.arguments?.getString(ScreenKey.toolbarTitle)

        RadioListScreenSetup(
            listType = listType ?: RadioListEnums.Local.name,
            tagName = tagName ?: "",
            languageName = languageName ?: "",
            countryName = countryName ?: "",
            toolbarTitle = toolbarTitle ?: "",
            navigationRoute = navigateRoute(navController)
        )
    }
}

@Composable
private fun navigateRoute(navController: NavController): (navigationRoute: String) -> Unit =
    {
        Timber.d(" radioAppNavigation it = $it")
        if (it == "back") {
            navController.popBackStack()
        } else {
            navController.navigate(it)
        }
    }