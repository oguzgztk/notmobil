package com.example.notuygulamasi.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Settings repository interface
 */
interface SettingsRepository {
    suspend fun getTheme(): Flow<String>
    suspend fun setTheme(theme: String)
    suspend fun getSyncEnabled(): Flow<Boolean>
    suspend fun setSyncEnabled(enabled: Boolean)
    suspend fun getNotificationEnabled(): Flow<Boolean>
    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun getGeminiApiKey(): Flow<String?>
    suspend fun setGeminiApiKey(apiKey: String)
}

