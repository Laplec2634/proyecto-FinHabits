package com.example.proyecto_finhabits.viewmodel

import androidx.lifecycle.*
import com.example.proyecto_finhabits.data.entities.Category
import com.example.proyecto_finhabits.data.entities.Transaction
import com.example.proyecto_finhabits.data.entities.TransactionType
import com.example.proyecto_finhabits.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class TransactionViewModel(private val repo: TransactionRepository) : ViewModel() {

    val allTransactions = repo.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val latestTransactions = repo.latestTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val totalIncome = repo.totalIncome.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val totalExpenses = repo.totalExpenses.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    val balance = combine(totalIncome, totalExpenses) { inc, exp -> inc - exp }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // For reports — selected period
    private val _reportPeriod = MutableStateFlow(ReportPeriod.MONTH)
    val reportPeriod: StateFlow<ReportPeriod> = _reportPeriod

    val reportTransactions: StateFlow<List<Transaction>> = _reportPeriod.flatMapLatest { period ->
        val (start, end) = period.range()
        repo.getByPeriod(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reportIncome: StateFlow<Double> = _reportPeriod.flatMapLatest { period ->
        val (start, end) = period.range()
        repo.getIncomeByPeriod(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val reportExpenses: StateFlow<Double> = _reportPeriod.flatMapLatest { period ->
        val (start, end) = period.range()
        repo.getExpensesByPeriod(start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun setReportPeriod(p: ReportPeriod) { _reportPeriod.value = p }

    fun insert(amount: Double, type: TransactionType, category: Category, description: String, date: Long) {
        viewModelScope.launch {
            repo.insert(Transaction(amount = amount, type = type, category = category, description = description, date = date))
        }
    }

    fun update(t: Transaction) { viewModelScope.launch { repo.update(t) } }
    fun delete(t: Transaction) { viewModelScope.launch { repo.delete(t) } }
}

enum class ReportPeriod(val label: String) {
    WEEK("Semana") {
        override fun range(): Pair<Long, Long> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
            val end = cal.timeInMillis
            cal.add(Calendar.DAY_OF_YEAR, -6)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
            return Pair(cal.timeInMillis, end)
        }
    },
    MONTH("Mes") {
        override fun range(): Pair<Long, Long> {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
            val end = cal.timeInMillis
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
            return Pair(cal.timeInMillis, end)
        }
    };
    abstract fun range(): Pair<Long, Long>
}

class TransactionViewModelFactory(private val repo: TransactionRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TransactionViewModel(repo) as T
}
