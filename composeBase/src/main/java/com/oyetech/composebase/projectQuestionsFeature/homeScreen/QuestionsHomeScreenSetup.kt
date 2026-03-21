package com.oyetech.composebase.projectQuestionsFeature.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.experimental.loginOperations.LoginOperationSmallButtonSetup
import com.oyetech.composebase.projectQuestionsFeature.ScreenKey.toolbarTitle
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.java.KoinJavaComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsHomeScreenSetup(
    modifier: Modifier = Modifier,
//    userUiState: LoginOperationUiState,
//    onUserEvent: (LoginOperationEvent) -> (Unit),
) {
    val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(NavigationUseCase::class.java)

    BaseScaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = toolbarTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            })
        },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(text = LanguageKey.home)
                Button(onClick = {
                    // Use NavigationUseCase pattern as requested
                    navigationUseCase.navigateTo(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
                }) {
                    Text(text = "Create Question")
                }

                // YENİ EKLENEN BUTON
                Button(onClick = {
                    navigationUseCase.navigateTo(QuestionAppProjectRoutes.QuestionFormScreen.route)
                }) {
                    Text(text = "Question Form")
                }
                LoginOperationSmallButtonSetup()
            }
        }
    )
}

@Composable
@Preview
private fun QuestionsHomeScreenPreview() {
    QuestionsHomeScreenSetup()
}
