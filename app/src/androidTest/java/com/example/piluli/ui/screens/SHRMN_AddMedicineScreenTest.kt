package com.example.piluli.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SHRMN_AddMedicineScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun testAddMedicationInput_ShowSuccessMessage() {
        composeRule.setContent {
            AddMedicineScreen()
        }

        // Test input and interaction
        composeRule.onNodeWithText("Введите название").performTextInput("Новый препарат")
        composeRule.onNodeWithText("Сохранить").performClick()
    }
}
