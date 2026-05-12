package com.example.proyecto_finhabits.data.repository

import com.example.proyecto_finhabits.data.dao.HabitDao
import com.example.proyecto_finhabits.data.entities.Habit
import com.example.proyecto_finhabits.data.entities.HabitCompletion
import kotlinx.coroutines.flow.Flow
import java.util.*

class HabitRepository(private val dao: HabitDao) {

    val allHabits: Flow<List<Habit>> = dao.getAllHabits()
    val activeHabits: Flow<List<Habit>> = dao.getActiveHabits()

    suspend fun insert(h: Habit) = dao.insert(h)
    suspend fun update(h: Habit) = dao.update(h)
    suspend fun delete(h: Habit) = dao.delete(h)
    suspend fun getById(id: Long) = dao.getById(id)

    fun getCompletions(habitId: Long) = dao.getCompletions(habitId)

    suspend fun toggleCompletion(habit: Habit) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val dayEnd = cal.timeInMillis

        val alreadyDone = dao.isCompletedToday(habit.id, dayStart, dayEnd)
        if (alreadyDone > 0) {
            dao.deleteCompletionInRange(habit.id, dayStart, dayEnd)
            dao.update(habit.copy(streak = maxOf(0, habit.streak - 1)))
        } else {
            dao.insertCompletion(HabitCompletion(habitId = habit.id))
            dao.update(habit.copy(streak = habit.streak + 1))
        }
    }

    suspend fun isCompletedToday(habitId: Long): Boolean {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        val dayStart = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val dayEnd = cal.timeInMillis
        return dao.isCompletedToday(habitId, dayStart, dayEnd) > 0
    }

    suspend fun getWeeklyCompletionRate(habitId: Long): Float {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -6)
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        val count = dao.countCompletionsInRange(habitId, start, end)
        return count / 7f
    }
}
