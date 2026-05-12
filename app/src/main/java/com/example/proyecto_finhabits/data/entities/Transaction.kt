package com.example.proyecto_finhabits.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType { INCOME, EXPENSE }

enum class Category(val label: String, val emoji: String) {
    FOOD("Alimentación", "🍔"),
    TRANSPORT("Transporte", "🚌"),
    ENTERTAINMENT("Entretenimiento", "🎬"),
    HEALTH("Salud", "💊"),
    EDUCATION("Educación", "📚"),
    SAVINGS("Ahorro", "💰"),
    OTHER("Otros", "📦")
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val description: String = "",
    val date: Long = System.currentTimeMillis()  // epoch millis
)
