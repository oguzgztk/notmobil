package com.example.notuygulamasi.domain.model

import java.util.Date

/**
 * Not domain model
 */
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val isSynced: Boolean = false,
    val tags: List<String> = emptyList(),
    val location: LocationData? = null,
    val sensorData: SensorData? = null
)

/**
 * Konum verisi
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

/**
 * Sensör verisi
 */
data class SensorData(
    val accelerometer: AccelerometerData? = null,
    val gyroscope: GyroscopeData? = null,
    val ambient: AmbientData? = null
)

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class GyroscopeData(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class AmbientData(
    val temperature: Float? = null,
    val humidity: Float? = null,
    val pressure: Float? = null,
    val timestamp: Long
)

