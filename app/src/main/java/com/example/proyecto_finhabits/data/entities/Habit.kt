package com.example.proyecto_finhabits.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HabitFrequency(val label: String) {
    DAILY("Diario"),
    WEEKLY("Semanal")
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val isActive: Boolean = true,
    val streak: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
