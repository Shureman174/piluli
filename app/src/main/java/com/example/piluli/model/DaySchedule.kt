package com.example.piluli.model

import java.time.LocalDate

data class Medication(
    val name: String,
    val dosage: String,
    val timeSlot: String // "Утро", "День", "Вечер"
)

data class DaySchedule(
    val date: LocalDate,
    val medications: List<Medication>,
    val pillCountAvailable: Int,
    val pillsTakenToday: Int = 0
)
