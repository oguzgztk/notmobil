package com.example.notuygulamasi.data.repository

import com.example.notuygulamasi.data.local.datastore.PreferencesManager
import com.example.notuygulamasi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Settings Repository implementasyonu
 */
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : SettingsRepository {
    
    override suspend fun getTheme(): Flow<String> {
        return preferencesManager.theme
    }
    
    override suspend fun setTheme(theme: String) {
        preferencesManager.setTheme(theme)
    }
    
    override suspend fun getSyncEnabled(): Flow<Boolean> {
        return preferencesManager.syncEnabled
    }
    
    override suspend fun setSyncEnabled(enabled: Boolean) {
        preferencesManager.setSyncEnabled(enabled)
    }
    
    override suspend fun getNotificationEnabled(): Flow<Boolean> {
        return preferencesManager.notificationEnabled
    }
    
    override suspend fun setNotificationEnabled(enabled: Boolean) {
        preferencesManager.setNotificationEnabled(enabled)
    }
    
    override suspend fun getGeminiApiKey(): Flow<String?> {
        return preferencesManager.getGeminiApiKey()
    }
    
    override suspend fun setGeminiApiKey(apiKey: String) {
        preferencesManager.saveGeminiApiKey(apiKey)
    }
}

