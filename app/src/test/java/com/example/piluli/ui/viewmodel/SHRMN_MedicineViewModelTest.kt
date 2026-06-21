package com.example.piluli.ui.viewmodel

import com.example.piluli.data.MedicineRepository
import com.example.piluli.model.Medicine
import com.example.piluli.viewmodel.AddMedicineViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `test medicine addition calls repository`() = runTest {
        coEvery { mockRepo.insert(any()) } just Runs
        
        viewModel.addMedicine()
        
        // Advance dispatcher to execute launch block
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { mockRepo.insert(any()) }
    }
}
