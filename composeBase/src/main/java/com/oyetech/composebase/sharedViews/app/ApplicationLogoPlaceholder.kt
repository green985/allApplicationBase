package com.oyetech.composebase.sharedViews.app

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.oyetech.languageModule.keyset.LanguageKey

@Composable
fun ApplicationLogoPlaceholder() {
    val context = LocalContext.current

    val appIconDrawable = remember {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getApplicationIcon(context.packageName)
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getApplicationIcon(context.packageName)
            }
        } catch (e: Exception) {
            null
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (appIconDrawable != null) {
                val bitmap = remember(appIconDrawable) { appIconDrawable.toBitmap() }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = LanguageKey.appName
                )
            } else {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = LanguageKey.appName,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
