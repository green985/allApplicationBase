package com.oyetech.viewmodule

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val lightViewColorScheme = lightColorScheme()
private val darkViewColorScheme = darkColorScheme()

@Composable
fun ViewModuleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkViewColorScheme else lightViewColorScheme,
        content = content,
    )
}
