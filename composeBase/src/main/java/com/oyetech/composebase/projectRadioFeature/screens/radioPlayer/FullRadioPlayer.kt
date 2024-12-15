package com.oyetech.composebase.projectRadioFeature.screens.radioPlayer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oyetech.composebase.R
import com.oyetech.composebase.projectRadioFeature.RadioDimensions
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioPlayerEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIEvent
import com.oyetech.composebase.projectRadioFeature.screens.radioListScreen.RadioUIState
import com.oyetech.composebase.projectRadioFeature.screens.views.RadioLogoView
import com.oyetech.composebase.projectRadioFeature.screens.views.RadioTagChipView
import com.oyetech.models.radioProject.radioModels.PlayState.Playing
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("FunctionNaming", "LongMethod")
@Composable
fun FullRadioPlayer(
    modifier: Modifier = Modifier,
    uiState: RadioUIState,
    radioPlayerEvent: (RadioUIEvent) -> Unit,
    navigationRoute: (navigationRoute: String) -> Unit = {},
) {

    Column(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        RadioLogoView(uiState.faviconUrl, size = RadioDimensions.radioBigLogoSize)


        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = uiState.radioName,
            style = MaterialTheme.typography.displaySmall,
        )

        if (uiState.contentTitle.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = uiState.contentTitle,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        RadioTagChipView(
            uiState.tags,
            navigationRoute,
            { tag -> radioPlayerEvent.invoke(RadioUIEvent.TagSelected(tag)) })


        Spacer(modifier = Modifier.height(32.dp))

        val buttonTint = MaterialTheme.colorScheme.primary

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_skip_previous_circle),
                contentDescription = "Previous",
                tint = buttonTint,
                modifier = Modifier
                    .size(RadioDimensions.radioFullScreenButtonWidthHeight)
                    .clickable { radioPlayerEvent(RadioPlayerEvent.Previous) },
            )

            val playButtonImage = if (uiState.playerState == Playing) {
                R.drawable.ic_pause_circle
            } else {
                R.drawable.ic_play_circle
            }


            Icon(
                painter = painterResource(id = playButtonImage),
                contentDescription = "Play/Pause",
                tint = buttonTint,
                modifier = Modifier
                    .size(RadioDimensions.radioFullScreenButtonWidthHeight)
                    .clickable { radioPlayerEvent(RadioPlayerEvent.Play(uiState)) },
            )

            val favoriteButtonImage = if (uiState.isFavorite) {
                R.drawable.ic_favorite_fill
            } else {
                R.drawable.ic_favorite_border
            }
            Icon(
                painter = painterResource(id = favoriteButtonImage),
                contentDescription = "Favorite",
                tint = buttonTint,
                modifier = Modifier
                    .size(64.dp)
                    .clickable { radioPlayerEvent(RadioUIEvent.AddFavorite(uiState)) },

                )

            Icon(
                painter = painterResource(id = R.drawable.ic_skip_next_circle),
                contentDescription = "Next",
                tint = buttonTint,
                modifier = Modifier
                    .size(RadioDimensions.radioFullScreenButtonWidthHeight)
                    .clickable { radioPlayerEvent(RadioPlayerEvent.Next) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun FullRadioPlayerPreview() {
    FullRadioPlayer(
        uiState = RadioUIState(
            faviconUrl = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png",
            radioName = "Radio Name",
            contentTitle = "denemeee",
            tags = listOf("Tag1", "Tag2").toImmutableList(),
            isPlayingView = true
        ),
        radioPlayerEvent = {}
    )
}