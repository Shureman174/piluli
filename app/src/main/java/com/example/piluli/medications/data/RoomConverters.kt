package com.example.piluli.medications.data

import androidx.room.TypeConverter

class RoomConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString(separator = "|") { it.trim() }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("|").map { it.trim() }.filter { it.isNotBlank() }
    }
}
