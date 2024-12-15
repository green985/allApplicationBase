package com.oyetech.composebase.projectRadioFeature.screens.views

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.projectRadioFeature.navigationRoutes.RadioAppProjectRoutes
import com.oyetech.composebase.projectRadioFeature.screens.ScreenKey
import com.oyetech.models.radioProject.enums.RadioListEnums.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun RadioTagChipView(
    tags: ImmutableList<String>,
    navigationRoute: (navigationRoute: String) -> Unit = {},
    onTagSelected: (tag: String) -> Unit = {},
) {

    if (tags.isNotEmpty() && !tags.firstOrNull().equals("")) {
        FlowRow() {
            tags.forEach { tag ->
                SuggestionChip(
                    modifier = Modifier
                        .height(32.dp)
                        .padding(end = 2.dp, bottom = 2.dp),
                    enabled = true,
                    label = {
                        Text(
                            text = tag,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    shape = RoundedCornerShape(RadioDimensions.radioTagCornerRadius),
                    onClick = {
                        onTagSelected(tag)
                        navigationRoute.invoke(
                            RadioAppProjectRoutes.RadioList.withArgs(
                                ScreenKey.listType to Tag.name,
                                ScreenKey.tagName to tag,
                                ScreenKey.toolbarTitle to tag
                            )
                        )
                    }
                )
            }
        }
    }


}

@Preview
@Composable
private fun RadioTagChipViewPreview() {
    RadioTagChipView(
        tags = persistentListOf("Pop", "Rock", "Jazz", "Classic"),
        onTagSelected = {}
    )
}