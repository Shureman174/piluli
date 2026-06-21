package com.example.piluli

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.piluli.data.MedicineDatabase
import com.example.piluli.ui.viewmodel.AppViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class AppViewModelTest {

    private lateinit var viewModel: AppViewModel
    private lateinit var database: MedicineDatabase

    @Before
    fun setUp() {
        // Используем встроенный TaskExecutor для выполнения тестов синхронно
        ArchTaskExecutor.getInstance().setDelegate(object : ArchTaskExecutor.Delegate() {
            override fun doInBackground(runnable: Runnable) = runnable.run()
        })
        
        database = Room.inMemoryDatabaseBuilder(getInstrumentation().targetContext, MedicineDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        viewModel = AppViewModel(getInstrumentation().targetContext.applicationContext as Application)
    }

    @After
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
        database.close()
    }

    @Test
    fun testSelectedDateStateFlow() {
        val selectedDate = LocalDate.of(2023, 10, 1)
        viewModel.setSelectedDate(selectedDate)
        assertEquals(selectedDate, viewModel.selectedDate.value)
    }

    @Test
    fun testDayScheduleWithSettings() {
        viewModel.saveSettings(pillCount = 4, currentPill = 0, hour = 8, minute = 30, isLoop = true)

        val daySchedule = viewModel.daySchedule.value

        assertEquals(LocalDate.now(), daySchedule.date)
        assertEquals(4, daySchedule.pillCount)
    }
}