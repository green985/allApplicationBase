package com.oyetech.composebase.projectQuestionsFeature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppBottomNavigationView
import com.oyetech.composebase.projectQuestionsFeature.navigation.QuestionAppProjectRoutes
import com.oyetech.composebase.projectQuestionsFeature.navigation.questionAppNavigation
import com.oyetech.composebase.projectRadioFeature.theme.RadioAppTheme

class QuestionMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RadioAppTheme {
                val navController = rememberNavController()
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    modifier = androidx.compose.ui.Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = androidx.compose.ui.Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = QuestionAppProjectRoutes.questionApplicationBottomTabNavList.first().path,
                        ) {
                            questionAppNavigation(navController)
                        }
                    }
                    QuestionAppBottomNavigationView(navController = navController)
                }
            }
        }
    }
}
