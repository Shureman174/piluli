package com.example.piluli.medications.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.piluli.medications.ui.nav.Routes
import com.example.piluli.medications.ui.screens.HomeWeekScreen
import com.example.piluli.medications.ui.screens.JournalScreen
import com.example.piluli.medications.ui.screens.MedListScreen
import com.example.piluli.medications.ui.screens.MedicationDetailScreen
import com.example.piluli.medications.ui.screens.MedicationEditorScreen
import com.example.piluli.medications.ui.screens.SettingsScreen

@Composable
fun MedApp(modifier: Modifier = Modifier) {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.Home.route,
        modifier = modifier
    ) {
        composable(Routes.Home.route) {
            val vm: com.example.piluli.medications.ui.HomeViewModel = viewModel()
            HomeWeekScreen(
                viewModel = vm,
                onOpenMedications = { nav.navigate(Routes.MedList.route) },
                onOpenSettings = { nav.navigate(Routes.Settings.route) }
            )
        }
        composable(Routes.Settings.route) {
            val vm: com.example.piluli.medications.ui.SettingsViewModel = viewModel()
            SettingsScreen(viewModel = vm, onBack = { nav.popBackStack() })
        }
        composable(Routes.MedList.route) {
            val vm: MedicationsViewModel = viewModel()
            MedListScreen(
                viewModel = vm,
                onAdd = { nav.navigate(Routes.MedAdd.route) },
                onOpen = { id -> nav.navigate(Routes.MedDetail.create(id)) },
                onEdit = { id -> nav.navigate(Routes.MedEdit.create(id)) },
                onOpenJournal = { nav.navigate(Routes.Journal.route) },
            )
        }
        composable(Routes.MedAdd.route) {
            val vm: MedicationEditorViewModel = viewModel()
            MedicationEditorScreen(
                viewModel = vm,
                title = "Новый препарат",
                onBack = { nav.popBackStack() },
                onSaved = { id ->
                    nav.navigate(Routes.MedDetail.create(id)) {
                        popUpTo(Routes.MedList.route)
                    }
                }
            )
        }
        composable(
            route = Routes.MedEdit.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: return@composable
            val vm: MedicationEditorViewModel = viewModel()
            androidx.compose.runtime.LaunchedEffect(id) { vm.loadForEdit(id) }
            MedicationEditorScreen(
                viewModel = vm,
                title = "Редактирование",
                onBack = { nav.popBackStack() },
                onSaved = { savedId ->
                    nav.navigate(Routes.MedDetail.create(savedId)) {
                        popUpTo(Routes.MedList.route)
                    }
                }
            )
        }
        composable(
            route = Routes.MedDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: return@composable
            val vm: MedicationDetailViewModel = viewModel()
            MedicationDetailScreen(
                medicationId = id,
                viewModel = vm,
                onBack = { nav.popBackStack() }
            )
        }
        composable(Routes.Journal.route) {
            val vm: JournalViewModel = viewModel()
            JournalScreen(viewModel = vm, onBack = { nav.popBackStack() })
        }
    }
}

