package com.example.soundmodeswitcher.presentation

import android.content.Context
import android.media.AudioManager
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material.Button
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class SoundTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        // Создаем клик-действие
        val clickable = ModifiersBuilders.Clickable.Builder()
            .setId("click_toggle")
            .setOnClick(
                ActionBuilders.LaunchAction.Builder()
                    .setAndroidActivity(
                        ActionBuilders.AndroidActivity.Builder()
                            .setPackageName(packageName)
                            .setClassName("$packageName.presentation.MainActivity")
                            .build()
                    ).build()
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

        // Кнопка плитки
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