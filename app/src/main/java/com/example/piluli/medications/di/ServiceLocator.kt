package com.example.piluli.medications.di

import android.content.Context
import com.example.piluli.medications.data.AppDatabase
import com.example.piluli.medications.data.MedicationRepository

object ServiceLocator {
    @Volatile
    private var repository: MedicationRepository? = null

    fun medicationRepository(context: Context): MedicationRepository {
        return repository ?: synchronized(this) {
            repository ?: run {
                val db = AppDatabase.getInstance(context.applicationContext)
                MedicationRepository(
                    context = context.applicationContext,
                    medicationDao = db.medicationDao(),
                    intakeLogDao = db.intakeLogDao(),
                    doseSlotDao = db.doseSlotDao(),
                )
            }.also { repository = it }
        }
    }
}

