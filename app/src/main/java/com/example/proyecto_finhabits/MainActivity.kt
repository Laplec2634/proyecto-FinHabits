package com.example.proyecto_finhabits

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.proyecto_finhabits.ui.navigation.FinHabitsNavGraph
import com.example.proyecto_finhabits.ui.theme.FinHabitsTheme
import com.example.proyecto_finhabits.viewmodel.HabitViewModel
import com.example.proyecto_finhabits.viewmodel.HabitViewModelFactory
import com.example.proyecto_finhabits.viewmodel.TransactionViewModel
import com.example.proyecto_finhabits.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as FinHabitsApp

        setContent {
            FinHabitsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val transactionVM: TransactionViewModel = viewModel(
                        factory = TransactionViewModelFactory(app.transactionRepository)
                    )
                    val habitVM: HabitViewModel = viewModel(
                        factory = HabitViewModelFactory(app.habitRepository)
                    )

                    FinHabitsNavGraph(
                        navController = navController,
                        transactionVM = transactionVM,
                        habitVM = habitVM
                    )
                }
            }
        }
    }
}