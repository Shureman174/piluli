package com.piluli.app.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.piluli.app.data.model.CalendarDay
import androidx.compose.ui.Alignment

/**
 * UI компонент для отображения полоски календаря из 7 дней.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarStrip(
    days: List<CalendarDay>,
    onDateClick: (CalendarDay) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        days.forEach { day ->
            val contentColor = when {
                day.isSelected -> MaterialTheme.colorScheme.primary
                day.isToday -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            }

            Surface(
                onClick = { onDateClick(day) },
                shape = MaterialTheme.shapes.small,
                color = if (day.isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${day.date.dayOfMonth}",
                    color = contentColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
