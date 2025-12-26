package com.example.notuygulamasi.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Connectivity repository interface
 */
interface ConnectivityRepository {
    suspend fun isWifiConnected(): Flow<Boolean>
    suspend fun isBluetoothEnabled(): Flow<Boolean>
    suspend fun isNfcEnabled(): Flow<Boolean>
    suspend fun scanBluetoothDevices(): Flow<List<String>>
}

