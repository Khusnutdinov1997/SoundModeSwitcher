package com.example.soundmodeswitcher

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager

class SoundManager(
    private val context: Context
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun hasNotificationPolicyAccess(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun getCurrentRingerMode(): Int {
        return audioManager.ringerMode
    }

    fun toggleSoundMode(): Int? {
        if (!hasNotificationPolicyAccess()) {
            return null
        }

        val newMode = when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> AudioManager.RINGER_MODE_VIBRATE
            else -> AudioManager.RINGER_MODE_NORMAL
        }

        audioManager.ringerMode = newMode
        return newMode
    }
}