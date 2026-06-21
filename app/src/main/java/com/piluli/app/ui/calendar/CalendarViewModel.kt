package com.piluli.app.ui.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.piluli.app.data.model.CalendarDay
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * ViewModel для управления состоянием календаря в верхнем блоке экрана.
 */
class CalendarViewModel : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _calendarDays = MutableStateFlow(generateCalendarStrip())
    val calendarDays: StateFlow<List<CalendarDay>> = _calendarDays.asStateFlow()

    /**
     * Генерирует список из 7 дней, начиная с понедельника текущей недели.
     */
    private fun generateCalendarStrip(): List<CalendarDay> {
        val today = LocalDate.now()
        // Если сегодня воскресенье — берем сегодняшний день как начало недели (воскресенье) иначе — предыдущий понедельник.
        val firstDayOfWeek = if (today.dayOfWeek == java.time.DayOfWeek.SUNDAY) {
            today
        } else {
            today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
        }

        return (0 until 7).map { i ->
            val date = firstDayOfWeek.plusDays(i.toLong())
            CalendarDay(
                date = date,
                isSelected = date == _selectedDate.value,
                isToday = date == today
            )
        }
    }

    /**
     * Переключает выбранный день в календаре.
     */
    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }
}
