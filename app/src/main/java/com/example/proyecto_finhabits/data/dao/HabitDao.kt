package com.example.proyecto_finhabits.data.dao

import androidx.room.*
import com.example.proyecto_finhabits.data.entities.Habit
import com.example.proyecto_finhabits.data.entities.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getById(id: Long): Habit?

    // Completions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedAt DESC")
    fun getCompletions(habitId: Long): Flow<List<HabitCompletion>>

    @Query("""
        SELECT COUNT(*) FROM habit_completions 
        WHERE habitId = :habitId 
        AND completedAt BETWEEN :start AND :end
    """)
    suspend fun countCompletionsInRange(habitId: Long, start: Long, end: Long): Int

    @Query("""
        SELECT COUNT(*) FROM habit_completions
        WHERE habitId = :habitId
        AND completedAt BETWEEN :dayStart AND :dayEnd
    """)
    suspend fun isCompletedToday(habitId: Long, dayStart: Long, dayEnd: Long): Int

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND completedAt BETWEEN :start AND :end")
    suspend fun deleteCompletionInRange(habitId: Long, start: Long, end: Long)
}
