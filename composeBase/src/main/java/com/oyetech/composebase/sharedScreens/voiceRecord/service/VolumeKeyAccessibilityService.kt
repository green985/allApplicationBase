package com.oyetech.composebase.sharedScreens.voiceRecord.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import timber.log.Timber

class VolumeKeyAccessibilityService : AccessibilityService() {
    private val doublePressDetector = DoublePressDetector(intervalMs = 450L)

    override fun onServiceConnected() {
        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED or
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        info.notificationTimeout = 0
        serviceInfo = info
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (doublePressDetector.onKey(VolumeKeyType.Up)) {
                    DigitalNoteActionDispatcher.dispatch(VolumeKeyType.Up)
                }
                return false
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (doublePressDetector.onKey(VolumeKeyType.Down)) {
                    DigitalNoteActionDispatcher.dispatch(VolumeKeyType.Down)
                }
                return false
            }
        }
        return false
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        Timber.d("onAccessibilityEvent: $p0")
    }

    override fun onInterrupt() {
        Timber.d("onInterrupt")
    }
}