package com.example.piluli.ui.viewmodel

import com.example.piluli.data.MedicineRepository
import com.example.piluli.viewmodel.AddMedicineViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SHRMN_MedicineViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepo = mockk<MedicineRepository>()
    private lateinit var viewModel: AddMedicineViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AddMedicineViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test medicine addition validation`() = runTest {
        // Test for empty name scenario
        viewModel.updateName("")
        viewModel.saveMedicine()
        
        assertNotNull(viewModel.uiState.value.error)
        assertEquals("Введите название лекарства", viewModel.uiState.value.error)
    }

    @Test
    fun `test success state on valid input`() = runTest {
        coEvery { mockRepo.addMedicine(any()) } returns Unit
        
        viewModel.updateName("Valid")
        viewModel.updateDosageValue(10.0)
        viewModel.updateDosageUnit("mg")
        
        viewModel.saveMedicine()
        
        // Advance dispatcher to execute launch block
        testDispatcher.scheduler.advanceUntilIdle()
        
        assert(viewModel.uiState.value.isSuccess)
    }
}
