package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.CareerRepository
import com.example.domain.model.CareerCategory
import com.example.domain.model.CareerItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CareerUiState(
    val categories: List<CareerCategory> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val careers: List<CareerItem> = emptyList(),
    val filteredCareers: List<CareerItem> = emptyList(),
    val selectedCareerDetail: CareerItem? = null,
    val isDetailBookmarked: Boolean = false,
    val isLoading: Boolean = false
)

class CareerViewModel(
    private val repository: CareerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareerUiState(isLoading = true))
    val uiState: StateFlow<CareerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val cats = repository.getCategories()
        _uiState.value = _uiState.value.copy(categories = cats)

        viewModelScope.launch {
            repository.allCareers.collect { careerList ->
                _uiState.value = _uiState.value.copy(
                    careers = careerList,
                    filteredCareers = careerList,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery
        val cat = _uiState.value.selectedCategory
        val results = repository.searchCareers(query, cat)
        _uiState.value = _uiState.value.copy(filteredCareers = results)
    }

    fun loadCareerDetail(careerId: String) {
        viewModelScope.launch {
            val career = repository.getCareerById(careerId)
            _uiState.value = _uiState.value.copy(selectedCareerDetail = career)
            if (career != null) {
                repository.isCareerBookmarked(career.id).collect { isBookmarked ->
                    _uiState.value = _uiState.value.copy(isDetailBookmarked = isBookmarked)
                }
            }
        }
    }

    fun toggleBookmark(career: CareerItem) {
        viewModelScope.launch {
            repository.toggleBookmark(career.id, career.title, career.category)
            _uiState.value = _uiState.value.copy(isDetailBookmarked = !_uiState.value.isDetailBookmarked)
        }
    }
}
