package com.example.myandroidproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myandroidproject.network.HabitRepository
import com.example.myandroidproject.network.models.HabitCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddHabitUiState(
    val name: String = "",
    val description: String = "",
    val goal: String = "",
    val categories: List<HabitCategory> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isLoadingCategories: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

class AddHabitViewModel(application: Application) : AndroidViewModel(application) {
    private val habitRepository = HabitRepository()
    private val _uiState = MutableStateFlow(AddHabitUiState())
    val uiState: StateFlow<AddHabitUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        _uiState.update { it.copy(isLoadingCategories = true, error = null) }
        viewModelScope.launch {
            habitRepository.getCategories()
                .onSuccess { categories ->
                    _uiState.update { state ->
                        state.copy(
                            categories = categories,
                            selectedCategoryId = state.selectedCategoryId ?: categories.firstOrNull()?.id,
                            isLoadingCategories = false
                        )
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoadingCategories = false,
                            error = throwable.message ?: "Failed to load categories"
                        )
                    }
                }
        }
    }

    fun onNameChange(value: String) { _uiState.update { it.copy(name = value, error = null) } }
    fun onDescriptionChange(value: String) { _uiState.update { it.copy(description = value, error = null) } }
    fun onGoalChange(value: String) { _uiState.update { it.copy(goal = value, error = null) } }
    fun onCategorySelected(id: Long) { _uiState.update { it.copy(selectedCategoryId = id, error = null) } }

    fun createHabit() {
        val state = _uiState.value
        if (state.isSaving) return

        val validationError = when {
            state.name.isBlank() -> "Habit name is required"
            state.selectedCategoryId == null -> "Choose a category"
            state.goal.isBlank() -> "Goal is required"
            else -> null
        }

        if (validationError != null) {
            _uiState.update { it.copy(error = validationError) }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            habitRepository.createHabit(
                name = state.name.trim(),
                description = state.description.trim(),
                categoryId = state.selectedCategoryId!!,
                goal = state.goal.trim()
            ).onSuccess {
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = throwable.message ?: "Failed to create habit"
                    )
                }
            }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(isSaved = false) }
    }
}