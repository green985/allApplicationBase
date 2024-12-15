package com.oyetech.composebase.base

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScaffold(
    showTopBar: Boolean = true,
    showBottomBar: Boolean = false,
    showFAB: Boolean = false,
    onFabClick: (() -> Unit)? = null,
    onNavigateUp: (() -> Unit)? = null,
    topBarContent: @Composable (() -> Unit)? = null,
    bottomBarContent: @Composable (() -> Unit)? = null,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
        NavigationBarDefaults.windowInsets
    ),
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = Modifier.background(Color.Transparent),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
            NavigationBarDefaults.windowInsets
        ),
        topBar = {
            if (showTopBar) {
                topBarContent?.invoke()
            }
        },
        bottomBar = {
            if (showBottomBar && bottomBarContent != null) {
                bottomBarContent()
            }
        },
        floatingActionButton = {
            if (showFAB && onFabClick != null) {
                FloatingActionButton(onClick = onFabClick) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "FAB"
                    )
                }
            }
        },
        content = { paddingValues ->
            content(paddingValues)
        }
    )
}

@Composable
fun SampleScreen() {
    BaseScaffold(
        showTopBar = true,
        showBottomBar = true,
        showFAB = true,
        onFabClick = { /* FAB tıklama işlemleri */ },
        onNavigateUp = { /* Geri tuşu tıklama işlemleri */ },
        bottomBarContent = {
        }
    ) { padding ->
        // Scaffold içeriği burada
        Column(modifier = Modifier.padding(padding)) {
            Text(text = "Hello, Compose!")
            // Diğer içerikler
        }
    }
}

