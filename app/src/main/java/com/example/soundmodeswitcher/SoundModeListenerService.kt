package com.example.soundmodeswitcher

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class SoundModeListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        super.onMessageReceived(messageEvent)

        if(messageEvent.path == TOGGLE_SOUND_PATH){
            val soundManager = SoundManager(applicationContext)
            soundManager.toggleSoundMode()
        }
    }

    companion object{
        const val TOGGLE_SOUND_PATH = "/toggle_sound_mode"
    }
}