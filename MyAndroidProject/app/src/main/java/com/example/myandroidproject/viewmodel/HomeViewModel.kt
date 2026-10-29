package com.example.myandroidproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myandroidproject.network.HabitRepository
import com.example.myandroidproject.network.ScheduleRepository
import com.example.myandroidproject.network.models.HabitResponse
import com.example.myandroidproject.network.models.ScheduleResponse
import com.example.myandroidproject.utils.DateTimeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val today: String = DateTimeUtils.todayDate(),
    val isLoading: Boolean = true,
    val schedules: List<ScheduleResponse> = emptyList(),
    val habits: List<HabitResponse> = emptyList(),
    val error: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val habitRepository = HabitRepository()
    private val scheduleRepository = ScheduleRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadToday() {
        val today = DateTimeUtils.todayDate()
        _uiState.update { it.copy(today = today, isLoading = true, error = null) }

        viewModelScope.launch {
            val habitsDeferred = async { habitRepository.getHabits() }
            val schedulesDeferred = async { scheduleRepository.getScheduleByDay(today) }

            val habitsResult = habitsDeferred.await()
            val schedulesResult = schedulesDeferred.await()

            val error = schedulesResult.exceptionOrNull()?.message ?: habitsResult.exceptionOrNull()?.message

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    habits = habitsResult.getOrDefault(state.habits)
                        .distinctBy { it.id }, // Habit lista duplikáció szűrése
                    schedules = schedulesResult.getOrDefault(state.schedules)
                        .distinctBy { it.id } // Schedule lista duplikáció szűrése
                        .sortedBy { schedule -> schedule.startTime ?: "" },
                    error = error
                )
            }
        }
    }
}