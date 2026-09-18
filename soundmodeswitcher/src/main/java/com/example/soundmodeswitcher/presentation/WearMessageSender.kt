package com.example.soundmodeswitcher.presentation

import android.content.Context
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.tasks.await

class WearMessageSender(
    context: Context
) {

    private val messageClient = Wearable.getMessageClient(context)
    private val nodeClient = Wearable.getNodeClient(context)

    companion object{

        const val TOGGLE_SOUND_PATH ="/toggle_sound_mode"
    }

    suspend fun sendToggleCommand(): Boolean{
        return try {
            val nodes = nodeClient.connectedNodes.await()

            for (node in nodes){
                messageClient.sendMessage(node.id, TOGGLE_SOUND_PATH, ByteArray(0)).await()
            }
            nodes.isNotEmpty()
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }
}