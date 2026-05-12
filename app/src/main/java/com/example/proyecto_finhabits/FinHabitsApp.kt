package com.example.proyecto_finhabits

import android.app.Application
import com.example.proyecto_finhabits.data.FinHabitsDatabase
import com.example.proyecto_finhabits.data.repository.HabitRepository
import com.example.proyecto_finhabits.data.repository.TransactionRepository

class FinHabitsApp : Application() {
    val database by lazy { FinHabitsDatabase.getDatabase(this) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val habitRepository by lazy { HabitRepository(database.habitDao()) }
}
