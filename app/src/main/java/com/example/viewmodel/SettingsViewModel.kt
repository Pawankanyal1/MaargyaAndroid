package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferencesRepository
import com.example.data.remote.ApiConfig
import com.example.data.remote.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: String = "SYSTEM",
    val backendUrl: String = ApiConfig.DEFAULT_BASE_URL,
    val isOnline: Boolean = true,
    val isUrlSavedSuccess: Boolean = false
)

class SettingsViewModel(
    private val userPrefs: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPrefs.themeMode.collect { mode ->
                _uiState.value = _uiState.value.copy(themeMode = mode)
            }
        }
        viewModelScope.launch {
            userPrefs.backendUrl.collect { url ->
                _uiState.value = _uiState.value.copy(backendUrl = url)
                ApiConfig.updateBaseUrl(url)
            }
        }
        viewModelScope.launch {
            networkMonitor.isOnline.collect { online ->
                _uiState.value = _uiState.value.copy(isOnline = online)
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            userPrefs.setThemeMode(mode)
        }
    }

    fun updateBackendUrl(newUrl: String) {
        viewModelScope.launch {
            userPrefs.setBackendUrl(newUrl)
            ApiConfig.updateBaseUrl(newUrl)
            _uiState.value = _uiState.value.copy(isUrlSavedSuccess = true)
        }
    }
}
