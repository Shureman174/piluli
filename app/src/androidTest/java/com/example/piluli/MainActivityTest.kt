package com.example.piluli

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.junit.Before
import org.junit.Test

class MainActivityTest {

    @Before
    fun setUp() {
        // Проверка корректного запуска активности
        ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testSelectedDateTextView() {
        onView(withId(R.id.selectedDateTextView)).check(matches(withText("2023-10-01")))
    }

    @Test
    fun testDayScheduleListView() {
        // Здесь вы можете добавить код для проверки ListView, если у вас есть такой UI компонент
    }
}