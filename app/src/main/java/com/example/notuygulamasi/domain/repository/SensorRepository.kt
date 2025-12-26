package com.example.notuygulamasi.domain.repository

import com.example.notuygulamasi.domain.model.LocationData
import com.example.notuygulamasi.domain.model.SensorData
import kotlinx.coroutines.flow.Flow

/**
 * Sensor repository interface
 */
interface SensorRepository {
    suspend fun getCurrentLocation(): Flow<LocationData?>
    suspend fun getSensorData(): Flow<SensorData>
    suspend fun startLocationUpdates()
    suspend fun stopLocationUpdates()
    suspend fun startSensorUpdates()
    suspend fun stopSensorUpdates()
}

