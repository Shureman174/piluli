package com.example.piluli.model

import java.util.UUID

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val dosage: String = "",
    val frequency: String = ""
)
