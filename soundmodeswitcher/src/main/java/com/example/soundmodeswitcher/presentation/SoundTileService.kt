package com.example.soundmodeswitcher.presentation

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SoundTileService : TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        try {
            // Безопасно обрабатываем клик
            val state = requestParams.currentState
            val lastClickId = state.lastClickableId

            if (lastClickId == "click_toggle") {
                triggerHapticFeedback()
                SoundModeState.toggle()

                val messageSender = WearMessageSender(this)
                serviceScope.launch {
                    try {
                        messageSender.sendToggleCommand()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val clickable = ModifiersBuilders.Clickable.Builder()
            .setId("click_toggle")
            .setOnClick(
                ActionBuilders.LoadAction.Builder().build()
            ).build()

        val currentMode = SoundModeState.get()
        val icon = when (currentMode) {
            AudioManager.RINGER_MODE_NORMAL -> "🔔"
            AudioManager.RINGER_MODE_VIBRATE -> "📳"
            AudioManager.RINGER_MODE_SILENT -> "🔕"
            else -> "❔"
        }

        val textElement = Text.Builder(this, icon)
            .setTypography(Typography.TYPOGRAPHY_TITLE1)
            .build()

        val button = Button.Builder(this, clickable)
            .setContentDescription("Toggle Sound")
            .setCustomContent(textElement)
            .build()

        val layout = LayoutElementBuilders.Box.Builder()
            .addContent(button)
            .build()

        val timelineEntry = TimelineBuilders.TimelineEntry.Builder()
            .setLayout(LayoutElementBuilders.Layout.Builder().setRoot(layout).build())
            .build()

        val timeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(timelineEntry)
            .build()

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(timeline)
            .build()

        return Futures.immediateFuture(tile)
    }

    private fun triggerHapticFeedback() {
        try {
            val vibrator =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager =
                        getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                }

            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator.vibrate(effect)
        } catch (e: Exception) {
            // Если устройство не поддерживает вибрацию или нет разрешения — просто игнорируем, чтобы не ломать плитку
            e.printStackTrace()
        }
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<ResourceBuilders.Resources> {
        val resources = ResourceBuilders.Resources.Builder()
            .setVersion("1")
            .build()
        return Futures.immediateFuture(resources)
    }
}

fun requestSoundTileUpdate(context: Context) {
    TileService.getUpdater(context)
        .requestUpdate(SoundTileService::class.java)
}

@Preview(device = WearDevices.LARGE_ROUND)
fun soundTilePreview(context: Context): TilePreviewData {
    return TilePreviewData { request ->
        // Создаем иконку текущего режима
        val textElement = Text.Builder(context, "🔔")
            .setTypography(Typography.TYPOGRAPHY_TITLE1)
            .build()

        // Создаем кнопку
        val button = Button.Builder(context, null)
            .setContentDescription("Toggle Sound")
            .setCustomContent(textElement)
            .build()

        // Используем PrimaryLayout и передаем request.deviceConfiguration,
        // чтобы превью учитывало геометрию конкретного экрана часов
        val layout = PrimaryLayout.Builder(request.deviceConfiguration)
            .setContent(button)
            .build()

        val layoutObj = LayoutElementBuilders.Layout.Builder()
            .setRoot(layout)
            .build()

        TilePreviewHelper.singleTimelineEntryTileBuilder(layoutObj).build()
    }
}