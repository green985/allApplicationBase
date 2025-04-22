package com.oyetech.composebase.sharedScreens.userProfile.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.languageModule.keyset.LanguageKey

/**
Created by Erdi Özbek
-21.04.2025-
-14:43-
 **/

@Composable
fun ProfileBiograpyhyInputArea(
    isEditMode: Boolean = false, biographyText: String,
    onBiographyTextChange: (biographyText: String) -> Unit = {},
) {
    OutlinedTextField(
        readOnly = !isEditMode,
        value = biographyText,
        maxLines = 5,
        minLines = 2,
        onValueChange = { onBiographyTextChange(it) },
        label = { Text(LanguageKey.biographyInfoHint) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun ProfileBiographyInputAreaPreview() {
    ProfileBiograpyhyInputArea(
        biographyText = "This is a sample biography text.",
        onBiographyTextChange = {}
    )
}