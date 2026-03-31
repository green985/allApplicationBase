package com.oyetech.composebase.projectQuestionsFeature.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.projectQuestionsFeature.theme.AppColors
import com.oyetech.composebase.projectQuestionsFeature.theme.AppTextStyles
import com.oyetech.composebase.projectQuestionsFeature.theme.QuestionProjectViewAttrs

/**
Created by Erdi Özbek
-14.11.2024-
-15:45-
 **/

@Composable
fun SettingsSimpleNavigationView(
    modifier: Modifier = Modifier,
    settingsTitleName: String,
    onClick: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.surface)
            .padding(QuestionProjectViewAttrs.paddingTitleRow)
    ) {

        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = settingsTitleName,
            style = AppTextStyles.titleLarge,
        )
        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Filled.KeyboardArrowRight,
            tint = AppColors.textPrimary,
            contentDescription = "Settings",
            modifier = Modifier.clickable { onClick.invoke() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsSimpleNavigationViewPreview() {
    SettingsSimpleNavigationView(
        settingsTitleName = "Settings",
        onClick = {}
    )
}
