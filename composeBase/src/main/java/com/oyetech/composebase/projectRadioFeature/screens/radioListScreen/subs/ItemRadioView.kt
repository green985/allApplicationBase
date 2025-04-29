package com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.subs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent.Play
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.AddAlarm
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.CreateShortcut
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.ExpandItem
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.Share
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent.ToggleFavorite
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.navigationToTagList
import com.oyetech.composebase.projectRadioFeature.screens.views.RadioLogoView
import com.oyetech.composebase.projectRadioFeature.screens.views.RadioTagChipView
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

/**
Created by Erdi Özbek
-5.11.2024-
-23:17-
 **/

@Composable
fun ItemRadioView(
    uiState: RadioUIState,
    radioPlayerEvent: (RadioUIEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {

    ItemRadioStation(
        modifier = Modifier.clickable {
            radioPlayerEvent.invoke(Play(uiState))
        },
        radioName = uiState.radioName,
        faviconUrl = uiState.faviconUrl,
        tags = uiState.tags,
        isFavorite = uiState.isFavorite,
        isExpanded = uiState.isExpanded,
        isSelectedView = uiState.isSelectedView,
        onFavoriteClick = { radioPlayerEvent(ToggleFavorite(uiState)) },
        onExpandClick = { radioPlayerEvent(ExpandItem(uiState)) },
        onAlarmClick = { radioPlayerEvent(AddAlarm) },
        onShortcutClick = { radioPlayerEvent(CreateShortcut) },
        onShareClick = { radioPlayerEvent(Share) },
        onTagSelected = { tag ->
            radioPlayerEvent.invoke(RadioUIEvent.TagSelected(tag))
            navigationToTagList(navigationRoute, tag)
        },
        navigationRoute = navigationRoute
    )

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalGlideComposeApi::class)
@Suppress("LongParameterList", "FunctionNaming", "UnusedParameter")
@Composable
fun ItemRadioStation(
    modifier: Modifier = Modifier,
    radioName: String,
    faviconUrl: String,
    tags: ImmutableList<String>,
    isFavorite: Boolean,
    isExpanded: Boolean,
    onFavoriteClick: () -> Unit,
    onExpandClick: () -> Unit,
    onAlarmClick: () -> Unit,
    onShortcutClick: () -> Unit,
    onShareClick: () -> Unit,
    onTagSelected: (tag: String) -> Unit = {},
    navigationRoute: (navigationRoute: String) -> Unit = {},
    isSelectedView: Boolean,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .then(
                if (isSelectedView) {
                    Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                } else Modifier
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        // Radyo Bilgileri
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Radyo Logosu

            RadioLogoView(faviconUrl = faviconUrl)

            Spacer(modifier = Modifier.width(8.dp))

            // Radyo İsmi
            Text(
                text = radioName,
                fontSize = RadioDimensions.bigTextSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Favori Butonu
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

//            // Genişletme Butonu
//            IconButton(onClick = onExpandClick) {
//                Icon(
//                    modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
//                    imageVector = Icons.Default.ArrowDropDown,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
        }

        Spacer(modifier = Modifier.height(RadioDimensions.lineHeight))

        // Etiketler
        if (isExpanded) {
            Spacer(modifier = Modifier.height(4.dp))
            RadioTagChipView(tags, navigationRoute, onTagSelected)

            Spacer(modifier = Modifier.height(4.dp))

            // İşlem Butonları
            // TODO for feature
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//            ) {
//                IconButton(onClick = onAlarmClick) {
//                    Icon(
//                        modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
//                        painter = painterResource(R.drawable.ic_add_alarm),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                IconButton(onClick = onShortcutClick) {
//                    Icon(
//                        modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
//                        painter = painterResource(R.drawable.ic_baseline_app_shortcut_24),
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//
//                IconButton(onClick = onShareClick) {
//                    Icon(
//                        modifier = Modifier.size(RadioDimensions.radioLogoSmallWidthHeight),
//                        imageVector = Icons.Default.Share,
//                        contentDescription = null,
//                        tint = MaterialTheme.colorScheme.primary
//                    )
//                }
//            }
        }
    }
}

@Preview(name = "Expanded")
@Composable
fun ItemRadioViewPreview() {
    ItemRadioStation(
        radioName = "Radyo 1",
        faviconUrl = "https://www.google.com",
        tags = persistentListOf("Pop", "Rock"),
        isFavorite = false,
        isExpanded = true,
        onFavoriteClick = {},
        onExpandClick = {},
        onAlarmClick = {},
        onShortcutClick = {},
        onShareClick = {},
        isSelectedView = false
    )

}

@Preview(name = "Collapsed")
@Composable
fun ItemRadioViewPreviewCollapsed() {
    ItemRadioStation(
        radioName = "Radyo 1",
        faviconUrl = "https://www.google.com",
        tags = persistentListOf("Pop", "Rock"),
        isFavorite = true,
        isExpanded = false,
        onFavoriteClick = {},
        onExpandClick = {},
        onAlarmClick = {},
        onShortcutClick = {},
        onShareClick = {},
        isSelectedView = false
    )

}

@Preview(name = "IsFavoriteFalse")
@Composable
fun ItemRadioViewPreviewIsFavoriteFalse() {
    ItemRadioStation(
        radioName = "Radyo 1",
        faviconUrl = "https://www.google.com",
        tags = persistentListOf("Pop"),
        isFavorite = false,
        isExpanded = false,
        onFavoriteClick = {},
        onExpandClick = {},
        onAlarmClick = {},
        onShortcutClick = {},
        onShareClick = {},
        isSelectedView = true
    )

}

