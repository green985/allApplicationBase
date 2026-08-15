package com.oyetech.watchAppFeatures

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.LayoutElement
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TypeBuilders
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.layout.column
import androidx.wear.protolayout.material3.ButtonGroupDefaults.DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.Material3TileService
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.presentation.WearMainActivity
import java.time.Instant
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
        return if (tick.isFinished) buildFinishedLayout() else buildCountdownLayout(tick.remainingSeconds)
    }

    private fun MaterialScope.buildCountdownLayout(remainingSeconds: Int): LayoutElement {
        val endEpochSeconds = System.currentTimeMillis() / 1000L + remainingSeconds
        val endInstant = DynamicBuilders.DynamicInstant.withSecondsPrecision(
            Instant.ofEpochSecond(endEpochSeconds)
        )

        // DynamicInstant.platformTimeWithSecondsPrecision() updates every second automatically —
        // no requestUpdate needed for the live countdown.
        val totalSeconds = DynamicBuilders.DynamicInstant.platformTimeWithSecondsPrecision()
            .durationUntil(endInstant)
            .toIntSeconds()

        val pad2 = DynamicBuilders.DynamicInt32.IntFormatter.Builder()
            .setMinIntegerDigits(2)
            .build()

        val countdownStr = totalSeconds
            .div(DynamicBuilders.DynamicInt32.constant(SECONDS_IN_MINUTE)).format(pad2)
            .concat(DynamicBuilders.DynamicString.constant(":"))
            .concat(totalSeconds.rem(DynamicBuilders.DynamicInt32.constant(SECONDS_IN_MINUTE)).format(pad2))

        val timerText = LayoutElementBuilders.Text.Builder()
            .setText(
                TypeBuilders.StringProp.Builder("00:00")
                    .setDynamicValue(countdownStr)
                    .build()
            )
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(COUNTDOWN_TEXT_SP).build())
                    .setWeight(
                        LayoutElementBuilders.FontWeightProp.Builder()
                            .setValue(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .build()
                    )
                    .setColor(colorScheme.primary.prop)
                    .build()
            )
            .build()

        return primaryLayout(
            mainSlot = { timerText },
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

    private fun MaterialScope.buildFinishedLayout(): LayoutElement {
        return primaryLayout(
            mainSlot = { text("Bitti".layoutString) },
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
                        row1.forEach { (seconds, number, unit) ->
                            buttonGroupItem {
                                durationButton(
                                    number = number,
                                    unit = unit,
                                    onClick = protoLayoutScope.clickable(
                                        pendingIntent = buildOpenAppPendingIntent(seconds),
                                        id = "duration_$seconds",
                                    ),
                                )
                            }
                        }
                    },
                    DEFAULT_SPACER_BETWEEN_BUTTON_GROUPS,
                    buttonGroup {
                        row2.forEach { (seconds, number, unit) ->
                            buttonGroupItem {
                                durationButton(
                                    number = number,
                                    unit = unit,
                                    onClick = protoLayoutScope.clickable(
                                        pendingIntent = buildOpenAppPendingIntent(seconds),
                                        id = "duration_$seconds",
                                    ),
                                )
                            }
                        }
                    },
                    width = DimensionBuilders.expand(),
                    height = DimensionBuilders.expand(),
                )
            },
        )
    }

    private fun MaterialScope.durationButton(
        number: String,
        unit: String,
        onClick: ModifiersBuilders.Clickable,
    ): LayoutElement {
        val numberText = LayoutElementBuilders.Text.Builder()
            .setText(number)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(NUMBER_TEXT_SP).build())
                    .setWeight(
                        LayoutElementBuilders.FontWeightProp.Builder()
                            .setValue(LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .build()
                    )
                    .setColor(colorScheme.onPrimaryContainer.prop)
                    .build()
            )
            .build()

        val unitText = LayoutElementBuilders.Text.Builder()
            .setText(unit)
            .setFontStyle(
                LayoutElementBuilders.FontStyle.Builder()
                    .setSize(DimensionBuilders.SpProp.Builder().setValue(UNIT_TEXT_SP).build())
                    .setColor(colorScheme.onPrimaryContainer.prop)
                    .build()
            )
            .build()

        val innerColumn = LayoutElementBuilders.Column.Builder()
            .addContent(numberText)
            .addContent(unitText)
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.wrap())
            .setHeight(DimensionBuilders.wrap())
            .build()

        val modifiers = ModifiersBuilders.Modifiers.Builder()
            .setClickable(onClick)
            .setBackground(
                ModifiersBuilders.Background.Builder()
                    .setColor(colorScheme.primaryContainer.prop)
                    .setCorner(
                        ModifiersBuilders.Corner.Builder()
                            .setRadius(DimensionBuilders.dp(BUTTON_CORNER_DP))
                            .build()
                    )
                    .build()
            )
            .build()

        return LayoutElementBuilders.Box.Builder()
            .setModifiers(modifiers)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .setHorizontalAlignment(LayoutElementBuilders.HORIZONTAL_ALIGN_CENTER)
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .addContent(innerColumn)
            .build()
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
        private data class DurationItem(val seconds: Int, val number: String, val unit: String)

        private val DURATION_ITEMS = listOf(
            DurationItem(10, "10", "sn"),
            DurationItem(2 * 60, "2", "dk"),
            DurationItem(5 * 60, "5", "dk"),
            DurationItem(10 * 60, "10", "dk"),
            DurationItem(15 * 60, "15", "dk"),
            DurationItem(20 * 60, "20", "dk"),
        )
        private const val ROW_SIZE = 3
        private const val SECONDS_IN_MINUTE = 60
        private const val ACTIVE_REFRESH_MS = 30_000L  // dynamic expression handles live tick
        private const val IDLE_REFRESH_MS = 10_000L
        private const val COUNTDOWN_TEXT_SP = 40f
        private const val NUMBER_TEXT_SP = 16f
        private const val UNIT_TEXT_SP = 10f
        private const val BUTTON_CORNER_DP = 50f
    }
}
