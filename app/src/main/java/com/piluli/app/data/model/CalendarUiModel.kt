package com.piluli.app.data.model

import java.time.LocalDate

/**
 * Модель представления одного дня в строке календаря.
 */
data class CalendarDay(
    val date: LocalDate,
    val isSelected: Boolean,
    val isToday: Boolean
)
