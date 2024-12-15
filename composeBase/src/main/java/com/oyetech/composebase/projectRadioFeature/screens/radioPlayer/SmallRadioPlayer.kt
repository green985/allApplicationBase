package com.oyetech.composebase.projectRadioFeature.screens.radioPlayer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.oyetech.composebase.R
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.views.RadioLogoView
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("FunctionNaming")
@Composable
fun SmallRadioPlayer(
    modifier: Modifier = Modifier,
    radioPlayerUIState: RadioUIState = RadioUIState(),
    radioPlayerEvent: (RadioPlayerEvent) -> Unit,
) {

    SmallRadioPlayerView(
        modifier = modifier,
        radioPlayerUIState,
        { radioPlayerEvent(RadioPlayerEvent.Play(it)) },
    )

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun SmallRadioPlayerView(
    modifier: Modifier = Modifier,
    uiState: RadioUIState,
    action: (RadioUIState) -> Unit,
    onExpand: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .height(RadioDimensions.radioSmallPlayerSizeHeight)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioLogoView(faviconUrl = uiState.faviconUrl)


            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = uiState.radioName, maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.contentTitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.contentTitle,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            val imageVector = if (uiState.playerState == Playing) {
                R.drawable.ic_pause_circle
            } else {
                R.drawable.ic_play_circle
            }


            Icon(
                painter = painterResource(id = imageVector),
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(RadioDimensions.radioLogoSmallWidthHeight)
                    .clickable {
                        action(uiState)
                    }
            )

        }

    }


}

@Preview
@Composable
fun SmallRadioPlayerPreview() {
    SmallRadioPlayerView(
        uiState = RadioUIState(
            radioName = "Radio Name",
            contentTitle = "deneme",
            tags = listOf("Tag 1", "Tag 2").toImmutableList(),
            isPlayingView = false,
            isSelectedView = false,
            isFavorite = false,
            faviconUrl = "",
        ),
        action = {},
        onExpand = {},
    )
}