package com.oyetech.composebase.baseViews.helper

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.oyetech.composebase.helpers.ProjectUtil
import kotlinx.collections.immutable.toImmutableList

@Composable
fun GenderSegmentedControl(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val genders = ProjectUtil.genderList.toImmutableList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(16.dp),
        ,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        genders.forEach { gender ->
            Button(
                onClick = { onGenderSelected(gender) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedGender == gender) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(text = gender.replaceFirstChar { it.uppercase() })
            }
        }
    }
}

@Preview
@Composable
private fun GenderSegmentedControlPreview() {
    GenderSegmentedControl("Male", {})
}