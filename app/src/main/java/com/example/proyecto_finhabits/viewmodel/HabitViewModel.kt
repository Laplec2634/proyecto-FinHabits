package com.example.proyecto_finhabits.viewmodel

import androidx.lifecycle.*
import com.example.proyecto_finhabits.data.entities.Habit
import com.example.proyecto_finhabits.data.entities.HabitFrequency
import com.example.proyecto_finhabits.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HabitWithProgress(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val weeklyRate: Float
)

class HabitViewModel(private val repo: HabitRepository) : ViewModel() {

    val allHabits = repo.allHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeHabits = repo.activeHabits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _habitsWithProgress = MutableStateFlow<List<HabitWithProgress>>(emptyList())
    val habitsWithProgress: StateFlow<List<HabitWithProgress>> = _habitsWithProgress

    init {
        viewModelScope.launch {
            repo.activeHabits.collect { habits ->
                _habitsWithProgress.value = habits.map { h ->
                    HabitWithProgress(
                        habit = h,
                        isCompletedToday = repo.isCompletedToday(h.id),
                        weeklyRate = repo.getWeeklyCompletionRate(h.id)
                    )
                }
            }
        }
    }

    fun insert(name: String, description: String, frequency: HabitFrequency) {
        viewModelScope.launch {
            repo.insert(Habit(name = name, description = description, frequency = frequency))
        }
    }

    fun update(h: Habit) { viewModelScope.launch { repo.update(h) } }

    fun delete(h: Habit) { viewModelScope.launch { repo.delete(h) } }

    fun toggleCompletion(h: Habit) {
        viewModelScope.launch {
            repo.toggleCompletion(h)
            // Refresh progress
            val habits = repo.activeHabits.first()
            _habitsWithProgress.value = habits.map { habit ->
                HabitWithProgress(
                    habit = habit,
                    isCompletedToday = repo.isCompletedToday(habit.id),
                    weeklyRate = repo.getWeeklyCompletionRate(habit.id)
                )
            }
        }
    }
}

class HabitViewModelFactory(private val repo: HabitRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = HabitViewModel(repo) as T
}
