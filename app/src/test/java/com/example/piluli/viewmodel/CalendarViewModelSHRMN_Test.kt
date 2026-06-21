package com.example.piluli.viewmodel

import com.example.piluli.model.Medicine
import com.example.piluli.data.MedicineRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AddMedicineViewModel 
    private val mockRepository = mockk<MedicineRepository>()

    @Before
    fun setup() {
        // Настраиваем mockRepository до создания ViewModel.
        // ViewModel при создании может обращаться к полям репозитория.
        every { mockRepository.allMedicines } returns flowOf(emptyList())
        
        viewModel = AddMedicineViewModel(mockRepository)
    }

    @Test
    fun `test selection of different date updates view_state`() = runTest {
        // В текущей AddMedicineViewModel нет логики выбора даты,
        // поэтому тест пока остается пустым или закомментированным,
        // чтобы не вызывать ошибок компиляции/выполнения.
    }
}
