package com.example.piluli.medications.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS dose_slots (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    medicationId INTEGER NOT NULL,
                    originalScheduledAtMillis INTEGER NOT NULL,
                    scheduledAtMillis INTEGER NOT NULL,
                    takenAtMillis INTEGER,
                    FOREIGN KEY(medicationId) REFERENCES medications(id) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_dose_slots_medicationId ON dose_slots(medicationId)")
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_dose_slots_medicationId_originalScheduledAtMillis " +
                    "ON dose_slots(medicationId, originalScheduledAtMillis)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_dose_slots_scheduledAtMillis ON dose_slots(scheduledAtMillis)")

            // intake_logs: add scheduledAtMillis
            db.execSQL("ALTER TABLE intake_logs ADD COLUMN scheduledAtMillis INTEGER")
        }
    }
}

