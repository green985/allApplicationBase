package com.oyetech.composebase.experimental.loginOperations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.experimental.authOperation.AuthOperationVM
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.AppShapes
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import org.koin.compose.koinInject
import kotlin.random.Random

@Composable
fun LoginOperationQuickScreen(
    authOperationVM: AuthOperationVM = koinInject(),
    onDismiss: () -> Unit,
) {
    val state by authOperationVM.authOperationState.collectAsState()
    var isVisible by remember { mutableStateOf(true) }

    if (!isVisible) return

    Box(
        modifier = Modifier
            .background(Color(0xFFEEEEEE))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto Create User",
                    style = AppTextStyles.titleMedium,
                )
                Text(
                    text = "✕",
                    modifier = Modifier.clickable { isVisible = false; onDismiss() },
                    style = AppTextStyles.titleMedium,
                    color = AppColors.error,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val randomId = Random.nextInt(1000, 9999)
                    authOperationVM.authOperationState.updateState {
                        copy(
                            username = "balbazar$randomId",
                            age = (Random.nextInt(18, 40)).toString(),
                            gender = if (randomId % 2 == 0) "male" else "female"
                        )
                    }
                },
                shape = AppShapes.roundedMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Otomatik Kullanıcı Oluştur")
            }

            if (state.isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator()
            }

            if (state.isError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = state.errorMessage,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
