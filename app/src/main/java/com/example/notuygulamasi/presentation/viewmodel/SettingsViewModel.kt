package com.example.notuygulamasi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notuygulamasi.domain.repository.AuthRepository
import com.example.notuygulamasi.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Ayarlar ViewModel
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getTheme().collect { theme ->
                _uiState.value = _uiState.value.copy(theme = theme)
            }
        }
        
        viewModelScope.launch {
            settingsRepository.getSyncEnabled().collect { syncEnabled ->
                _uiState.value = _uiState.value.copy(syncEnabled = syncEnabled)
            }
        }
        
        viewModelScope.launch {
            settingsRepository.getNotificationEnabled().collect { notificationEnabled ->
                _uiState.value = _uiState.value.copy(notificationEnabled = notificationEnabled)
            }
        }
        
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                currentUser = user?.email ?: "Kullanıcı bulunamadı"
            )
        }
        
        viewModelScope.launch {
            settingsRepository.getGeminiApiKey().collect { apiKey ->
                _uiState.value = _uiState.value.copy(geminiApiKey = apiKey ?: "")
            }
        }
    }
    
    fun setTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }
    
    fun setSyncEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setSyncEnabled(enabled)
        }
    }
    
    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationEnabled(enabled)
        }
    }
    
    fun setGeminiApiKey(apiKey: String) {
        viewModelScope.launch {
            settingsRepository.setGeminiApiKey(apiKey)
            _uiState.value = _uiState.value.copy(geminiApiKey = apiKey)
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = _uiState.value.copy(isLoggedOut = true)
        }
    }
}

data class SettingsUiState(
    val theme: String = "light",
    val syncEnabled: Boolean = true,
    val notificationEnabled: Boolean = true,
    val currentUser: String = "",
    val geminiApiKey: String = "",
    val isLoggedOut: Boolean = false
)

