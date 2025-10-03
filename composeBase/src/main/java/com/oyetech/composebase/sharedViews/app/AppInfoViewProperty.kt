package com.oyetech.composebase.sharedViews.app

import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import com.oyetech.languageModule.keyset.LanguageKey

@Composable
fun AppInfoViewProperty() {
    if (!LocalInspectionMode.current) {
        Column {
            Text(
                text = LanguageKey.appName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    val context = LocalContext.current
    val versionName: String = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val pkgInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
            pkgInfo.versionName ?: ""
        } else {
            @Suppress("DEPRECATION")
            val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            @Suppress("DEPRECATION")
            pkgInfo.versionName ?: ""
        }
    } catch (e: Exception) {
        ""
    }

    Column {
        Text(
            text = LanguageKey.appName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        if (versionName.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "v$versionName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
