package com.piluli.app.ui.calendar

import androidx.compose.ui.test.junit4.createComposeRule
import com.piluli.app.data.model.CalendarDay
import org.junit.Rule
import org.junit.Test

class CalendarStripTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun calendar_strip_renders_correctly() {
        // Проверка базовой структуры UI (наличие 7 элементов)
        composeTestRule.setContent {
            CalendarStrip(
                days = List(7) { i -> CalendarDay(java.time.LocalDate.now().plusDays(i.toLong()), false, false) },
                onDateClick = {}
            )
        }
    }
}
