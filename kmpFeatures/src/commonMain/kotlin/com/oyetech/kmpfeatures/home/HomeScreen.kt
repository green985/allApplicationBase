package com.oyetech.kmpfeatures.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oyetech.kmpfeatures.example.KmpFeaturesExampleOperation
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
) {
    val exampleOperation = koinInject<KmpFeaturesExampleOperation>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "KMP Features",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = exampleOperation.getWelcomeMessage(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
