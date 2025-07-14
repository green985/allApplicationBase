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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.base.updateState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Composable
fun LoginOperationQuickScreen(
    loginOperationVM: LoginOperationVM = koinViewModel(),
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state by loginOperationVM.loginOperationState.collectAsState()
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
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "✕",
                    modifier = Modifier
                        .clickable { isVisible = false; onDismiss() },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Auto populate and submit
                    val randomId = Random.nextInt(1000, 9999)
                    loginOperationVM.loginOperationState.updateState {
                        copy(
                            displayName = "balbazar$randomId",
                            age = (Random.nextInt(18, 40)).toString(),
                            gender = if (randomId % 2 == 0) "male" else "female"
                        )
                    }
                    scope.launch {
                        loginOperationVM.handleEvent(LoginOperationEvent.OnSubmit)
                    }
                },
                shape = RoundedCornerShape(12.dp),
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