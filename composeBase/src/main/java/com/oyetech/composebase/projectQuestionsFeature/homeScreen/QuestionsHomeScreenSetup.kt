package com.oyetech.composebase.projectQuestionsFeature.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.languageModule.keyset.LanguageKey
import org.koin.java.KoinJavaComponent

@Composable
fun QuestionsHomeScreenSetup(
    modifier: Modifier = Modifier,
//    userUiState: LoginOperationUiState,
//    onUserEvent: (LoginOperationEvent) -> (Unit),
) {
    val navigationUseCase: NavigationUseCase by KoinJavaComponent.inject(NavigationUseCase::class.java)

    BaseScaffold(
        topBar = {},
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(text = LanguageKey.home)
                Button(onClick = {
                    // Use NavigationUseCase pattern as requested
                    navigationUseCase.navigate(QuestionAppProjectRoutes.QuestionCreateQuestionPage.route)
                }) {
                    Text(text = "Create Question")
                }

                // todo enable login operation
//                if (!userUiState.isLogin) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(4.dp),
//                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ) {
//                        Button(onClick = {
//                            onUserEvent(LoginOperationEvent.LoginClicked)
//                        }) {
//                            Text(LanguageKey.commentLoginButtonText)
//                        }
//                    }
//                }
            }
        }
    )
}

@Composable
@Preview
private fun QuestionsHomeScreenPreview() {
    QuestionsHomeScreenSetup()
}
