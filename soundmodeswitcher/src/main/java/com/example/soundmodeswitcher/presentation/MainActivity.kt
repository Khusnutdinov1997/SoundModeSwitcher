/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.soundmodeswitcher.presentation

import android.media.AudioManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val messageSender = remember { WearMessageSender(context) }
    var currentMode by remember { mutableStateOf(SoundModeState.get()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (currentMode) {
                AudioManager.RINGER_MODE_NORMAL -> "🔔"
                AudioManager.RINGER_MODE_VIBRATE -> "📳"
                AudioManager.RINGER_MODE_SILENT -> "🔕"
                else -> "❔"
            },
            style = MaterialTheme.typography.display1
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    val success = messageSender.sendToggleCommand()
                    if (success) {
                        currentMode = SoundModeState.toggle()
                        requestSoundTileUpdate(context)

                        Toast.makeText(context, "Сигнал отправлен!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ошибка: телефон не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            Text(text = "Переключить")
        }
    }
}
