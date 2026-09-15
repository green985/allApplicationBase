package com.oyetech.kmpfeatures

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.oyetech.kmpfeatures.navigation.KmpFeaturesRoutes
import com.oyetech.kmpfeatures.navigation.KmpFeaturesNavGraph

@Composable
fun KmpFeaturesApp() {
    var currentRoute by remember { mutableStateOf(KmpFeaturesRoutes.Home) }

    MaterialTheme {
        KmpFeaturesNavGraph(
            route = currentRoute,
            onNavigate = { currentRoute = it },
        )
    }
}
