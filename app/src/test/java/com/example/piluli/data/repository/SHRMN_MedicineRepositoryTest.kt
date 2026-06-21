package com.example.piluli.data.repository

import com.example.piluli.model.Medicine
import com.example.piluli.data.MedicineDao
import com.example.piluli.data.MedicineRepository
import com.example.piluli.data.toEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SHRMN_MedicineRepositoryTest {

    private val mockDao = mockk<MedicineDao>()
    private lateinit var repository: MedicineRepository

    @Before
    fun setup() {
        // Настраиваем поведение для getAllMedicines, так как оно вызывается при создании репозитория
        every { mockDao.getAllMedicines() } returns flowOf(emptyList())
        repository = MedicineRepository(mockDao)
    }

    @Test
    fun `test medicine saving to database`() = runTest {
        val medicine = Medicine(
            id = "1",
            name = "Sample",
            dosage = "10 mg",
            frequency = "Daily"
        )
        coEvery { mockDao.insertMedicine(any()) } just Runs

        repository.insert(medicine)
        
        coVerify { mockDao.insertMedicine(match { it.id == "1" }) }
    }

    @Test
    fun `test get medicine by id`() = runTest {
        val medicine = Medicine(
            id = "1",
            name = "Sample",
            dosage = "10 mg",
            frequency = "Daily"
        )
        val entity = medicine.toEntity()
        every { mockDao.getMedicineById("1") } returns flowOf(entity)

        val result = repository.getMedicineById("1").first()
        assertEquals("Sample", result.name)
    }
}
