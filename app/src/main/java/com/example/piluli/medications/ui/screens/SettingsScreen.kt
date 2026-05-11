package com.example.piluli.medications.ui.screens

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.piluli.medications.ui.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
) {
    val s by viewModel.settings.collectAsState()
    val scroll = rememberScrollState()

    val ringtonePicker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<android.net.Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.update { it.copy(dueRingtoneUri = uri?.toString()) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )


        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(colors = CardDefaults.cardColors(), shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Скоро (уведомление в трее)", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = s.soonMinutes.toString(),
                        onValueChange = { v -> viewModel.update { it.copy(soonMinutes = v.toIntOrNull() ?: it.soonMinutes) } },
                        label = { Text("Предупреждать за (мин)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    ToggleRow(
                        title = "Звук",
                        checked = s.soonSoundEnabled,
                        onChecked = { viewModel.update { it.copy(soonSoundEnabled = it) } }
                    )
                    ToggleRow(
                        title = "Вибрация",
                        checked = s.soonVibrationEnabled,
                        onChecked = { viewModel.update { it.copy(soonVibrationEnabled = it) } }
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(), shape = MaterialTheme.shapes.large) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Время приёма (как будильник)", style = MaterialTheme.typography.titleMedium)
                    ToggleRow(
                        title = "Звук",
                        checked = s.dueSoundEnabled,
                        onChecked = { viewModel.update { it.copy(dueSoundEnabled = it) } }
                    )
                    ToggleRow(
                        title = "Вибрация",
                        checked = s.dueVibrationEnabled,
                        onChecked = { viewModel.update { it.copy(dueVibrationEnabled = it) } }
                    )
                    Button(
                        onClick = {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                            }
                            ringtonePicker.launch(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(Modifier.height(0.dp))
                        Text("Выбрать мелодию")
                    }

                    OutlinedTextField(
                        value = s.repeatIntervalMinutes.toString(),
                        onValueChange = { v -> viewModel.update { it.copy(repeatIntervalMinutes = v.toIntOrNull() ?: it.repeatIntervalMinutes) } },
                        label = { Text("Перерыв между повторами (мин)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = s.repeatCount.toString(),
                        onValueChange = { v -> viewModel.update { it.copy(repeatCount = v.toIntOrNull() ?: it.repeatCount) } },
                        label = { Text("Количество повторов") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Окно напоминания активно 1 минуту, затем повторяется по настройкам.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}

