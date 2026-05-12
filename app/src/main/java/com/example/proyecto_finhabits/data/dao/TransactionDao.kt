package com.example.proyecto_finhabits.data.dao

import androidx.room.*
import com.example.proyecto_finhabits.data.entities.Transaction
import com.example.proyecto_finhabits.data.entities.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 5")
    fun getLatestTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsByPeriod(start: Long, end: Long): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpenses(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end")
    fun getIncomeByPeriod(start: Long, end: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount),0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    fun getExpensesByPeriod(start: Long, end: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?
}
