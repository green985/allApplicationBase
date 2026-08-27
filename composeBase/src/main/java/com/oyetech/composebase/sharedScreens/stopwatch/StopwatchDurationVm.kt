package com.oyetech.composebase.sharedScreens.stopwatch

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import com.oyetech.composebase.base.BaseViewModel
import com.oyetech.composebase.base.updateState
import com.oyetech.composebase.navigator.AppRoute
import com.oyetech.domain.repository.stopwatch.StopwatchRecord
import com.oyetech.domain.repository.stopwatch.StopwatchRecordRepository
import com.oyetech.domain.repository.stopwatch.StopwatchSession
import com.oyetech.domain.useCases.NavigationUseCase
import com.oyetech.domain.useCases.StopwatchOperationUseCase
import com.oyetech.tools.coroutineHelper.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("TooManyFunctions")
class StopwatchDurationVm(
    appDispatchers: AppDispatchers,
    private val navigationUseCase: NavigationUseCase,
    private val stopwatchOperationUseCase: StopwatchOperationUseCase,
    private val stopwatchRecordRepository: StopwatchRecordRepository,
    private val appContext: Context,
) : BaseViewModel(appDispatchers) {

    val uiState = MutableStateFlow(StopwatchDurationUiState())

    init {
        Timber.d("StopwatchDurationVm: init")
        buildDurations()
        observeFinished()
    }

    private fun buildDurations() {
        val items = DURATION_SECONDS.map { seconds ->
            StopwatchDurationItem(
                session = StopwatchSession(durationSeconds = seconds),
                label = formatDurationLabel(seconds),
            )
        }
        uiState.updateState { copy(durations = items) }
    }

    private fun formatDurationLabel(durationSeconds: Int): String {
        return if (durationSeconds % SECONDS_IN_MINUTE == 0) {
            "${durationSeconds / SECONDS_IN_MINUTE} min"
        } else {
            "$durationSeconds sec"
        }
    }

    override fun onEvent(event: Any) {
        Timber.d("StopwatchDurationVm: onEvent -> $event")
        if (event is StopwatchDurationEvent) {
            when (event) {
                is StopwatchDurationEvent.OnDurationSelected -> onDurationSelected(event.session)
                StopwatchDurationEvent.OnExportClicked -> onExportClicked()
            }
        }
    }

    private fun onDurationSelected(session: StopwatchSession) {
        Timber.d("StopwatchDurationVm: onDurationSelected durationSeconds=${session.durationSeconds}")
        startTimerService(session)
        navigationUseCase.navigateTo(AppRoute.StopwatchScreen())
    }

    @SuppressLint("NewApi")
    private fun startTimerService(session: StopwatchSession) {
        Timber.d("StopwatchDurationVm: startTimerService durationSeconds=${session.durationSeconds} packageName=${appContext.packageName}")
        val intent = Intent("com.oyetech.wear.ACTION_START_TIMER").apply {
            setPackage(appContext.packageName)
            putExtra("seconds", session.durationSeconds)
        }
        ContextCompat.startForegroundService(appContext, intent)
    }

    private fun observeFinished() {
        Timber.d("StopwatchDurationVm: observeFinished started")
        viewModelScope.launch(getDispatcherIo()) {
            stopwatchOperationUseCase.onFinished.collect {
                Timber.d("StopwatchDurationVm: timer finished event received")
                uiState.updateState { copy(isTimerFinished = true) }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun onExportClicked() {
        Timber.d("StopwatchDurationVm: onExportClicked")
        viewModelScope.launch(getDispatcherIo()) {
            val records = stopwatchRecordRepository.getAll().first()
            val text = formatRecordsForExport(records)
            Timber.d("StopwatchDurationVm: export text=\n$text")
            val clipboard =
                appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("stopwatch_records", text))
            withContext(kotlinx.coroutines.Dispatchers.Main) {
                Toast.makeText(appContext, "Kopyalandı (${records.size} kayıt)", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun formatRecordsForExport(records: List<StopwatchRecord>): String {
        if (records.isEmpty()) return "Kayıt yok."
        val todayFmt = SimpleDateFormat("h:mm a", Locale.getDefault())
        val otherFmt = SimpleDateFormat("d MMM h:mm a", Locale.getDefault())
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        val sb = StringBuilder()
        records.forEach { r ->
            val dateLabel = if (r.startedAt >= todayStart) {
                "Today ${todayFmt.format(Date(r.startedAt))}"
            } else {
                otherFmt.format(Date(r.startedAt))
            }
            val dur = if (r.durationSeconds < SECONDS_IN_MINUTE) {
                "${r.durationSeconds}sn"
            } else {
                "${r.durationSeconds / SECONDS_IN_MINUTE}dk"
            }
            val tag = r.tag?.label ?: "-"
            val status = if (r.status.name == "FINISHED") "Bitti" else "İptal"
            sb.appendLine("[ $dateLabel ] $tag | $dur ($status)")
        }
        return sb.toString().trimEnd()
    }


    companion object {
        private const val SECONDS_IN_MINUTE = 60
        private val DURATION_SECONDS = listOf(10, 60 * 2, 5 * 60, 10 * 60, 15 * 60, 20 * 60)
    }
}
