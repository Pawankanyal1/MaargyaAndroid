package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.NotificationRepository
import com.example.domain.model.NotificationCategory
import com.example.domain.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationUiState(
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
    val allNotifications: List<NotificationItem> = emptyList(),
    val filteredNotifications: List<NotificationItem> = emptyList()
)

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            repository.notifications.collect { notifs ->
                _uiState.value = _uiState.value.copy(
                    allNotifications = notifs,
                    filteredNotifications = filterNotifications(notifs, _uiState.value.selectedCategory)
                )
            }
        }
    }

    fun selectCategory(category: NotificationCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            filteredNotifications = filterNotifications(_uiState.value.allNotifications, category)
        )
    }

    private fun filterNotifications(list: List<NotificationItem>, cat: NotificationCategory): List<NotificationItem> {
        return if (cat == NotificationCategory.ALL) list else list.filter { it.category == cat }
    }

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAsRead(id)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            repository.markAllAsRead()
        }
    }
}
