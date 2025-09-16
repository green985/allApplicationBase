package com.oyetech.composebase.sharedScreens.voiceRecord.service

interface VolumeDoublePressListener {
    fun onVolumeDoublePress(type: VolumeKeyType)
}

object DigitalNoteActionDispatcher {
    private val listeners = ArrayList<VolumeDoublePressListener>()

    fun register(listener: VolumeDoublePressListener) {
        if (!listeners.contains(listener)) listeners.add(listener)
    }

    fun unregister(listener: VolumeDoublePressListener) {
        listeners.remove(listener)
    }

    fun dispatch(type: VolumeKeyType) {
        listeners.forEach { it.onVolumeDoublePress(type) }
    }
}