package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CareerLabRepository
import com.example.domain.model.CareerDiscoverySummary
import com.example.domain.model.CareerLabDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CareerLabUiState(
    val days: List<CareerLabDay> = emptyList(),
    val selectedDay: CareerLabDay? = null,
    val selectedAnswers: Map<String, String> = emptyMap(),
    val summary: CareerDiscoverySummary? = null,
    val isSubmitting: Boolean = false,
    val isDayCompletedSuccess: Boolean = false
)

class CareerLabViewModel(
    private val repository: CareerLabRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareerLabUiState())
    val uiState: StateFlow<CareerLabUiState> = _uiState.asStateFlow()

    init {
        loadDays()
    }

    private fun loadDays() {
        viewModelScope.launch {
            repository.labDays.collect { daysList ->
                _uiState.value = _uiState.value.copy(days = daysList)
            }
        }
    }

    fun selectDay(dayNumber: Int) {
        viewModelScope.launch {
            val day = repository.getDay(dayNumber)
            _uiState.value = _uiState.value.copy(
                selectedDay = day,
                selectedAnswers = emptyMap(),
                isDayCompletedSuccess = false
            )
        }
    }

    fun setAnswer(questionId: String, answer: String) {
        val updated = _uiState.value.selectedAnswers.toMutableMap()
        updated[questionId] = answer
        _uiState.value = _uiState.value.copy(selectedAnswers = updated)
    }

    fun submitCurrentDay(dayNumber: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true)
            repository.completeDay(dayNumber, _uiState.value.selectedAnswers)
            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                isDayCompletedSuccess = true
            )
            onComplete()
        }
    }

    fun loadDiscoverySummary() {
        viewModelScope.launch {
            val summary = repository.getDiscoverySummary()
            _uiState.value = _uiState.value.copy(summary = summary)
        }
    }
}
