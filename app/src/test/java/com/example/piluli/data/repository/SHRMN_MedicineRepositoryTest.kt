package com.example.piluli.data.repository

import com.example.piluli.data.Medicine
import com.example.piluli.data.MedicineDao
import com.example.piluli.data.MedicineEntity
import com.example.piluli.data.MedicineRepository
import com.example.piluli.data.toEntity
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SHRMN_MedicineRepositoryTest {

    private val mockDao = mockk<MedicineDao>()
    private val repository = MedicineRepository(mockDao)

    @Test
    fun `test medicine saving to database`() = runTest {
        val medicine = Medicine(
            id = "1",
            name = "Sample",
            dosageValue = 10.0,
            dosageUnit = "mg",
            totalStock = 100,
            imagePath = null,
            notes = null
        )
        coEvery { mockDao.insert(any()) } returns 1L

        repository.addMedicine(medicine)
        
        coVerify { mockDao.insert(match { it.id == "1" }) }
    }

    @Test
    fun `test get medicine by id`() = runTest {
        val medicine = Medicine(
            id = "1",
            name = "Sample",
            dosageValue = 10.0,
            dosageUnit = "mg",
            totalStock = 100,
            imagePath = null,
            notes = null
        )
        val expectedEntity = medicine.toEntity()
        coEvery { mockDao.getById("1") } returns expectedEntity

        val result = repository.getMedicineById("1")
        assertEquals("Sample", result?.name)
    }
}
