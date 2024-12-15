package com.oyetech.composebase.projectRadioFeature.screens.views.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.oyetech.composebase.R

/**
Created by Erdi Özbek
-15.12.2024-
-13:42-
 **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateUsDialog(
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
) {

    BasicAlertDialog(onDismiss,
        modifier = Modifier

            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        content = {
            Column(modifier = Modifier) {
//                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.review_info_text),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onClick = onDismiss
                    ) {
                        Text(text = stringResource(R.string.no))
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f), onClick = onSuccess
                    ) {
                        Text(text = stringResource(R.string.yes))
                    }

                }
            }
        }

    )

}