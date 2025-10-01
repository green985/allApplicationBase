package com.oyetech.composebase.projectQuestionsFeature.homeScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.base.BaseScaffold
import com.oyetech.languageModule.keyset.LanguageKey

@Composable
fun QuestionsHomeScreenSetup(
    modifier: Modifier = Modifier,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {
    BaseScaffold(
        topBar = {},
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                Text(text = LanguageKey.home)
            }
        }
    )
}

@Composable
@Preview
private fun QuestionsHomeScreenPreview() {
    QuestionsHomeScreenSetup()
}
