package com.example.piluli.medications.reminder

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.piluli.medications.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FullScreenDoseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        val slotId = intent.getLongExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, -1L)
        setContent {
            MaterialTheme {
                val titleState = remember { mutableStateOf("Время приёма") }
                val medNameState = remember { mutableStateOf("") }

                LaunchedEffect(slotId) {
                    if (slotId <= 0) return@LaunchedEffect
                    val (title, medName) = withContext(Dispatchers.IO) {
                        val db = AppDatabase.getInstance(this@FullScreenDoseActivity)
                        val slot = db.doseSlotDao().getById(slotId)
                        val med = slot?.let { db.medicationDao().getById(it.medicationId) }
                        "Время приёма" to (med?.name ?: "")
                    }
                    titleState.value = title
                    medNameState.value = medName
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(titleState.value, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(medNameState.value, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            sendBroadcast(
                                android.content.Intent(this@FullScreenDoseActivity, DoseUserActionReceiver::class.java)
                                    .setAction(ReminderNotificationHelper.ACTION_TAKE)
                                    .putExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, slotId)
                            )
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Принять") }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendBroadcast(
                                android.content.Intent(this@FullScreenDoseActivity, DoseUserActionReceiver::class.java)
                                    .setAction(ReminderNotificationHelper.ACTION_POSTPONE)
                                    .putExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, slotId)
                                    .putExtra(ReminderNotificationHelper.EXTRA_MINUTES, 15)
                            )
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Отложить на 15 минут") }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendBroadcast(
                                android.content.Intent(this@FullScreenDoseActivity, DoseUserActionReceiver::class.java)
                                    .setAction(ReminderNotificationHelper.ACTION_POSTPONE)
                                    .putExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, slotId)
                                    .putExtra(ReminderNotificationHelper.EXTRA_MINUTES, 30)
                            )
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Отложить на 30 минут") }

                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            sendBroadcast(
                                android.content.Intent(this@FullScreenDoseActivity, DoseUserActionReceiver::class.java)
                                    .setAction(ReminderNotificationHelper.ACTION_POSTPONE)
                                    .putExtra(ReminderNotificationHelper.EXTRA_SLOT_ID, slotId)
                                    .putExtra(ReminderNotificationHelper.EXTRA_MINUTES, 60)
                            )
                            finish()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Отложить на 60 минут") }
                }
            }
        }
    }
}

