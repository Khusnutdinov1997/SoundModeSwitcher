package com.example.soundmodeswitcher

import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.soundmodeswitcher.ui.theme.SoundModeSwitcherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundModeSwitcherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SoundControlScreen()
                }
            }
        }
    }
}

@Composable
fun SoundControlScreen() {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    var hasPermission by remember { mutableStateOf(soundManager.hasNotificationPolicyAccess()) }
    var currentMode by remember { mutableStateOf(soundManager.getCurrentRingerMode()) }

    DisposableEffect(Unit) {
        val listener = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = soundManager.hasNotificationPolicyAccess()
                currentMode = soundManager.getCurrentRingerMode()
            }
        }
        val lifecycle = (context as ComponentActivity).lifecycle
        lifecycle.addObserver(listener)
        onDispose {
            lifecycle.removeObserver(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!hasPermission) {
            Text(
                text = "⚠️ Требуется доступ",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Для переключения режимов звука необходимо предоставить доступ к настройкам «Не беспокоить».",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            ) {
                Text("Предоставить доступ")
            }
        } else {
            val statusText = when (currentMode) {
                AudioManager.RINGER_MODE_NORMAL -> "🔔 Режим: Звук включен"
                AudioManager.RINGER_MODE_SILENT -> "🔕 Режим: Без звука"
                AudioManager.RINGER_MODE_VIBRATE -> "📳 Режим: Вибрация"
                else -> "Неизвестно"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val newMode = soundManager.toggleSoundMode()
                    if (newMode != null) {
                        currentMode = newMode
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = if (currentMode == AudioManager.RINGER_MODE_NORMAL) "Включить «Вибрацию»" else "Включить «Звук»"
                )
            }
        }
    }
}
