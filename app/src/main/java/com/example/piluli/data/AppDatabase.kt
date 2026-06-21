package com.example.piluli.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.piluli.data.MedicineEntity

@Database(entities = [MedicineEntity::class], version = 1)
abstract class AppDatabasePiluli : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabasePiluli? = null

        fun getDatabase(context: Context): AppDatabasePiluli {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabasePiluli::class.java,
                    "medicine_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
