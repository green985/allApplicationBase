package com.oyetech.watchAppFeatures

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.layout.column
import androidx.wear.protolayout.material3.ButtonGroupDefaults.DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.Material3TileService
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.presentation.WearMainActivity
import org.koin.java.KoinJavaComponent

class StopwatchDurationTileService : Material3TileService() {

    private val stopwatchOperationUseCase: StopwatchOperationUseCase by KoinJavaComponent.inject(
        StopwatchOperationUseCase::class.java
    )

    override suspend fun MaterialScope.tileResponse(requestParams: TileRequest): TileBuilders.Tile {
        val isActive = stopwatchOperationUseCase.hasActiveSession() ||
                stopwatchOperationUseCase.isFinishedPendingDisplay

        val layoutElement = if (isActive) buildActiveLayout() else buildDurationLayout()
        val freshnessMs = if (isActive) ACTIVE_REFRESH_MS else IDLE_REFRESH_MS

        return TileBuilders.Tile.Builder()
            .setTileTimeline(Timeline.fromLayoutElement(layoutElement))
            .setFreshnessIntervalMillis(freshnessMs)
            .build()
    }

    private fun MaterialScope.buildActiveLayout(): LayoutElement {
        val tick = stopwatchOperationUseCase.tickState.value
        val mins = tick.remainingSeconds / SECONDS_IN_MINUTE
        val secs = tick.remainingSeconds % SECONDS_IN_MINUTE
        val formattedTime = "${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        val statusText = if (tick.isFinished) "Bitti" else "Devam ediyor"

        return primaryLayout(
            titleSlot = {
                text(formattedTime.layoutString)
            },
            mainSlot = {
                text(statusText.layoutString)
            },
            bottomSlot = {
                textEdgeButton(
                    onClick = protoLayoutScope.clickable(
                        pendingIntent = buildOpenAppPendingIntent(0),
                        id = "open_app",
                    ),
                    labelContent = { text("Aç".layoutString) },
                )
            },
        )
    }

    private fun MaterialScope.buildDurationLayout(): LayoutElement {
        val row1 = DURATION_ITEMS.take(ROW_SIZE)
        val row2 = DURATION_ITEMS.drop(ROW_SIZE)

        return primaryLayout(
            titleSlot = {
                text("Süre".layoutString)
            },
            mainSlot = {
                column(
                    buttonGroup {
                        row1.forEach { (seconds, label) ->
                            buttonGroupItem {
                                textButton(
                                    onClick = protoLayoutScope.clickable(
                                        pendingIntent = buildOpenAppPendingIntent(seconds),
                                        id = "duration_$seconds",
                                    ),
                                    labelContent = { text(label.layoutString) },
                                    width = expand(),
                                    height = expand(),
                                )
                            }
                        }
                    },
                    DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS,
                    buttonGroup {
                        row2.forEach { (seconds, label) ->
                            buttonGroupItem {
                                textButton(
                                    onClick = protoLayoutScope.clickable(
                                        pendingIntent = buildOpenAppPendingIntent(seconds),
                                        id = "duration_$seconds",
                                    ),
                                    labelContent = { text(label.layoutString) },
                                    width = expand(),
                                    height = expand(),
                                )
                            }
                        }
                    },
                    width = expand(),
                    height = expand(),
                )
            },
        )
    }

    private fun buildOpenAppPendingIntent(seconds: Int): PendingIntent {
        val intent = Intent(this, WearMainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            if (seconds > 0) putExtra(WearMainActivity.EXTRA_START_SECONDS, seconds)
        }
        return PendingIntent.getActivity(
            this,
            seconds,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        private val DURATION_ITEMS = listOf(
            10 to "10 sn",
            2 * 60 to "2 dk",
            5 * 60 to "5 dk",
            10 * 60 to "10 dk",
            15 * 60 to "15 dk",
            20 * 60 to "20 dk",
        )
        private const val ROW_SIZE = 3
        private const val SECONDS_IN_MINUTE = 60
        private const val ACTIVE_REFRESH_MS = 1_000L
        private const val IDLE_REFRESH_MS = 10_000L
    }
}
