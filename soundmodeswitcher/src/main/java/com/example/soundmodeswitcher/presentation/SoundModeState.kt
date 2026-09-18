package com.example.soundmodeswitcher.presentation

import android.media.AudioManager

object SoundModeState {
    private var currentMode: Int = AudioManager.RINGER_MODE_NORMAL

    fun get(): Int = currentMode
    fun update(newMode: Int) {
        currentMode = newMode
    }

    fun toggle(): Int {
        currentMode = when(currentMode){
            AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
            else -> AudioManager.RINGER_MODE_NORMAL
        }
        return currentMode
    }
}