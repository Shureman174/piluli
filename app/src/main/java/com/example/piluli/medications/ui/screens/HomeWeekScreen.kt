package com.example.piluli.medications.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.piluli.medications.data.DateTimes
import com.example.piluli.medications.ui.HomeSlotUi
import com.example.piluli.medications.ui.HomeViewModel
import com.example.piluli.medications.ui.SlotStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeWeekScreen(
    viewModel: HomeViewModel,
    onOpenMedications: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val dayStart by viewModel.selectedDayStartMillis.collectAsState()
    val slots by viewModel.slots.collectAsState()

    LaunchedEffect(dayStart) {
        // убедимся, что слоты на выбранный день созданы
        viewModel.selectDay(dayStart)
    }

    var expandedSlotId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Сегодня") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Настройки")
                    }
                    Text(
                        "Препараты",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clickable { onOpenMedications() },
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            WeekStrip(
                selectedDayStartMillis = dayStart,
                onSelectDayStartMillis = { viewModel.selectDay(it) }
            )

            Spacer(Modifier.height(8.dp))

            if (slots.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет приёмов на выбранный день", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item { Spacer(Modifier.height(4.dp)) }
                    items(slots, key = { it.slot.id }) { slotUi ->
                        SlotCard(
                            ui = slotUi,
                            expanded = expandedSlotId == slotUi.slot.id,
                            onToggleExpanded = {
                                expandedSlotId = if (expandedSlotId == slotUi.slot.id) null else slotUi.slot.id
                            },
                            onTake = { viewModel.take(slotUi) },
                            onPostpone = { minutes ->
                                viewModel.postpone(
                                    slotId = slotUi.slot.id,
                                    currentScheduledAtMillis = slotUi.slot.scheduledAtMillis,
                                    minutes = minutes
                                )
                            }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun WeekStrip(
    selectedDayStartMillis: Long,
    onSelectDayStartMillis: (Long) -> Unit,
) {
    val cal = remember(selectedDayStartMillis) { Calendar.getInstance().apply { timeInMillis = selectedDayStartMillis } }
    // Понедельник как старт недели
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val diffToMonday = ((dayOfWeek + 5) % 7) // Mon=0 ... Sun=6
    cal.add(Calendar.DAY_OF_MONTH, -diffToMonday)

    val dayFmt = remember { SimpleDateFormat("EE", Locale("ru")) }
    val dateFmt = remember { SimpleDateFormat("d", Locale("ru")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(7) {
            val dayStart = DateTimes.startOfDayMillis(cal.timeInMillis)
            val isSelected = dayStart == selectedDayStartMillis
            val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            val fg = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(bg)
                    .clickable { onSelectDayStartMillis(dayStart) }
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(dayFmt.format(Date(dayStart)).uppercase(), color = fg, style = MaterialTheme.typography.labelSmall)
                Text(dateFmt.format(Date(dayStart)), color = fg, style = MaterialTheme.typography.titleMedium)
            }
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }
}

@Composable
private fun SlotCard(
    ui: HomeSlotUi,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onTake: () -> Unit,
    onPostpone: (Int) -> Unit,
) {
    val med = ui.medication
    val time = remember(ui.slot.scheduledAtMillis) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(ui.slot.scheduledAtMillis))
    }

    val (statusColor, statusIcon) = when (ui.status) {
        SlotStatus.PLANNED -> MaterialTheme.colorScheme.surfaceVariant to Icons.Default.Schedule
        SlotStatus.SOON -> Color(0xFFFFF3E0) to Icons.Default.Timer
        SlotStatus.OVERDUE -> Color(0xFFFFEBEE) to Icons.Default.Error
        SlotStatus.TAKEN -> Color(0xFFE8F5E9) to Icons.Default.CheckCircle
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onToggleExpanded() },
        colors = CardDefaults.cardColors(containerColor = statusColor),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(statusIcon, contentDescription = null)
                Text(time, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text(
                    when (ui.status) {
                        SlotStatus.PLANNED -> "Запланировано"
                        SlotStatus.SOON -> "Скоро"
                        SlotStatus.OVERDUE -> "Просрочено"
                        SlotStatus.TAKEN -> "Принято"
                    },
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = med?.photoUri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(med?.name ?: "—", style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    val subtitle = buildString {
                        append("${med?.intakesPerDay ?: 1} раз/день")
                        if (!med?.intakeTimes.isNullOrEmpty()) append(" • ${med?.intakeTimes?.joinToString().orEmpty()}")
                    }
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            if (expanded) {
                val isTakeEnabled = ui.slot.takenAtMillis == null
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton(text = "Принять", enabled = isTakeEnabled, onClick = onTake)
                    ActionButton(text = "Отложить +15", enabled = isTakeEnabled, onClick = { onPostpone(15) })
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionButton(text = "Отложить +30", enabled = isTakeEnabled, onClick = { onPostpone(30) })
                    ActionButton(text = "Отложить +60", enabled = isTakeEnabled, onClick = { onPostpone(60) })
                }
                if (!isTakeEnabled) {
                    Text("Уже отмечено — повторное принятие запрещено", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun RowScope.ActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.weight(1f)
    ) {
        Text(text)
    }
}

