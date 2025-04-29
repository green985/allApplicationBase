package com.oyetech.composebase.projectQuotesFeature.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.oyetech.composebase.projectQuotesFeature.QuotesDimensions
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.languageModule.keyset.LanguageKey
import com.oyetech.tools.contextHelper.getApplicationLogo

/**
Created by Erdi Özbek
-7.01.2025-
-23:22-
 **/

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemAuthorView(
    modifier: Modifier = Modifier,
    authorDisplayName: String,
    authorImage: String,
    onClick: () -> Unit,
) {

    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            val context = LocalContext.current
            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .clip(RoundedCornerShape(RadioDimensions.radioTagCornerRadius))
            ) {
                Box(Modifier.padding(4.dp)) {

                    if (LocalInspectionMode.current) {
                        Box(
                            Modifier
                                .size(QuotesDimensions.authorImageSize)
                                .background(
                                    MaterialTheme.colorScheme.primary
                                )
                                .clip(RoundedCornerShape(RadioDimensions.radioTagCornerRadius)),
                        )
                    } else {

                        GlideImage(
                            model = authorImage,
                            contentDescription = "RadioImage",
                            failure = placeholder(context.getApplicationLogo()),
                            modifier = Modifier
                                .size(QuotesDimensions.authorImageSize)
                                .clip(RoundedCornerShape(RadioDimensions.radioTagCornerRadius)),
                        )
                    }
                }
            }

            Text(
                text = authorDisplayName,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}

@Preview
@Composable
private fun PreviewItemAuthor() {
    ItemAuthorView(
        authorDisplayName = LanguageKey.errorText,
        authorImage = "",
        onClick = {},
    )
}