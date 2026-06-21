package com.example.piluli

import android.app.Application
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.example.piluli.data.AppDatabasePiluli
import com.example.piluli.ui.viewmodel.AppViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class AppViewModelTest {

    private lateinit var viewModel: AppViewModel
    private lateinit var database: AppDatabasePiluli

    @Before
    fun setUp() {
        // Используем встроенный TaskExecutor для выполнения тестов синхронно
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })
        
        database = Room.inMemoryDatabaseBuilder(getInstrumentation().targetContext, AppDatabasePiluli::class.java)
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
        assertEquals(4, daySchedule.pillCountAvailable)
    }
}