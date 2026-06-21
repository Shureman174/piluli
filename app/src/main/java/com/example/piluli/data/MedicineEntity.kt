package com.example.piluli.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.piluli.model.Medicine

@Entity(tableName = "medications")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dosageValue: Double? = null,
    val dosageUnit: String? = null,
    val totalStock: Int? = null,
    val imagePath: String? = null,
    val notes: String? = null
)

fun MedicineEntity.toDomain(): Medicine {
    return Medicine(
        id = id,
        name = name,
        dosage = "${dosageValue ?: ""} ${dosageUnit ?: ""}".trim(),
        frequency = ""
    )
}

fun Medicine.toEntity(): MedicineEntity {
    return MedicineEntity(
        id = id,
        name = name
        // Other fields will use defaults as they are not in the simple Medicine model
    )
}
