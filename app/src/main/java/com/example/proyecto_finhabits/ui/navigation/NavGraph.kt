package com.example.proyecto_finhabits.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.proyecto_finhabits.ui.screens.AddExpenseScreen
import com.example.proyecto_finhabits.ui.screens.AddHabitScreen
import com.example.proyecto_finhabits.ui.screens.HabitsScreen
import com.example.proyecto_finhabits.ui.screens.HomeScreen
import com.example.proyecto_finhabits.ui.screens.ReportsScreen
import com.example.proyecto_finhabits.ui.screens.SettingsScreen
import com.example.proyecto_finhabits.ui.screens.*
import com.example.proyecto_finhabits.viewmodel.HabitViewModel
import com.example.proyecto_finhabits.viewmodel.TransactionViewModel

object Routes {
    const val HOME      = "home"
    const val ADD_EXPENSE = "addExpense"
    const val EDIT_EXPENSE = "editExpense/{transactionId}"
    const val HABITS    = "habits"
    const val ADD_HABIT = "addHabit"
    const val EDIT_HABIT = "editHabit/{habitId}"
    const val REPORTS   = "reports"
    const val SETTINGS  = "settings"

    fun editExpense(id: Long) = "editExpense/$id"
    fun editHabit(id: Long) = "editHabit/$id"
}

@Composable
fun FinHabitsNavGraph(
    navController: NavHostController,
    transactionVM: TransactionViewModel,
    habitVM: HabitViewModel
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(navController, transactionVM, habitVM)
        }

        composable(Routes.ADD_EXPENSE) {
            AddExpenseScreen(navController, transactionVM, editTransaction = null)
        }

        composable(
            Routes.EDIT_EXPENSE,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("transactionId") ?: return@composable
            AddExpenseScreen(navController, transactionVM, editTransactionId = id)
        }

        composable(Routes.HABITS) {
            HabitsScreen(navController, habitVM)
        }

        composable(Routes.ADD_HABIT) {
            AddHabitScreen(navController, habitVM, editHabitId = null)
        }

        composable(
            Routes.EDIT_HABIT,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStack ->
            val id = backStack.arguments?.getLong("habitId") ?: return@composable
            AddHabitScreen(navController, habitVM, editHabitId = id)
        }

        composable(Routes.REPORTS) {
            ReportsScreen(navController, transactionVM, habitVM)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(navController)
        }
    }
}
