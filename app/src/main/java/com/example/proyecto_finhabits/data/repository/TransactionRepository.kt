package com.example.proyecto_finhabits.data.repository

import com.example.proyecto_finhabits.data.dao.TransactionDao
import com.example.proyecto_finhabits.data.entities.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val latestTransactions: Flow<List<Transaction>> = dao.getLatestTransactions()
    val totalIncome: Flow<Double> = dao.getTotalIncome()
    val totalExpenses: Flow<Double> = dao.getTotalExpenses()

    fun getByPeriod(start: Long, end: Long) = dao.getTransactionsByPeriod(start, end)
    fun getIncomeByPeriod(start: Long, end: Long) = dao.getIncomeByPeriod(start, end)
    fun getExpensesByPeriod(start: Long, end: Long) = dao.getExpensesByPeriod(start, end)

    suspend fun insert(t: Transaction) = dao.insert(t)
    suspend fun update(t: Transaction) = dao.update(t)
    suspend fun delete(t: Transaction) = dao.delete(t)
    suspend fun getById(id: Long) = dao.getById(id)
}
