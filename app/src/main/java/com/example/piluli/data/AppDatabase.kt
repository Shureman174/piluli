package com.example.piluli.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.piluli.data.MedicineEntity

@Database(entities = [MedicineEntity::class], version = 1)
abstract class AppDatabasePiluli : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
}
