package com.oyetech.composebase.helpers.viewProperties

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ImagePickerHandler(
    onImageSelected: (Uri) -> Unit,
): () -> Unit {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { onImageSelected(it) }
        }
    )

    return {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ImageOnly)
        )
    }
}
