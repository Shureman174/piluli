package com.example.piluli.medications.ui.nav

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Settings : Routes("settings")
    data object MedList : Routes("med_list")
    data object MedAdd : Routes("med_add")
    data object MedEdit : Routes("med_edit/{id}") {
        fun create(id: Long) = "med_edit/$id"
    }
    data object MedDetail : Routes("med_detail/{id}") {
        fun create(id: Long) = "med_detail/$id"
    }
    data object Journal : Routes("journal")
}

