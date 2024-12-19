package com.oyetech.composebase.projectRadioFeature.views.quotes.views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
Created by Erdi Özbek
-19.12.2024-
-22:55-
 **/

@Composable
fun ItemQuoteTagView(
    modifier: Modifier, tagName: String,
    onClick: () -> Unit = {},
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick() }
    ) {

        Text(
            modifier = Modifier.padding(8.dp),
            text = tagName,
            style = MaterialTheme.typography.titleMedium,
        )
    }


}

@Composable
@Preview(
    showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
fun ItemTagViewPreview() {
    ItemQuoteTagView(Modifier, "Tag Name", {})
}