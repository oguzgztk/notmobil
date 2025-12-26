package com.example.notuygulamasi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notuygulamasi.domain.model.Note
import java.util.Date

/**
 * Room Database için Note entity
 */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false,
    val tags: String = "", // JSON string olarak saklanacak
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
    val accelerometerX: Float? = null,
    val accelerometerY: Float? = null,
    val accelerometerZ: Float? = null,
    val accelerometerTimestamp: Long? = null,
    val gyroscopeX: Float? = null,
    val gyroscopeY: Float? = null,
    val gyroscopeZ: Float? = null,
    val gyroscopeTimestamp: Long? = null,
    val ambientTemperature: Float? = null,
    val ambientHumidity: Float? = null,
    val ambientPressure: Float? = null,
    val ambientTimestamp: Long? = null
) {
    fun toDomain(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            createdAt = Date(createdAt),
            updatedAt = Date(updatedAt),
            isSynced = isSynced,
            tags = if (tags.isNotEmpty()) tags.split(",") else emptyList(),
            location = if (latitude != null && longitude != null) {
                com.example.notuygulamasi.domain.model.LocationData(
                    latitude = latitude,
                    longitude = longitude,
                    address = address
                )
            } else null,
            sensorData = com.example.notuygulamasi.domain.model.SensorData(
                accelerometer = if (accelerometerX != null && accelerometerY != null && accelerometerZ != null) {
                    com.example.notuygulamasi.domain.model.AccelerometerData(
                        x = accelerometerX,
                        y = accelerometerY,
                        z = accelerometerZ,
                        timestamp = accelerometerTimestamp ?: 0L
                    )
                } else null,
                gyroscope = if (gyroscopeX != null && gyroscopeY != null && gyroscopeZ != null) {
                    com.example.notuygulamasi.domain.model.GyroscopeData(
                        x = gyroscopeX,
                        y = gyroscopeY,
                        z = gyroscopeZ,
                        timestamp = gyroscopeTimestamp ?: 0L
                    )
                } else null,
                ambient = if (ambientTemperature != null || ambientHumidity != null || ambientPressure != null) {
                    com.example.notuygulamasi.domain.model.AmbientData(
                        temperature = ambientTemperature,
                        humidity = ambientHumidity,
                        pressure = ambientPressure,
                        timestamp = ambientTimestamp ?: 0L
                    )
                } else null
            )
        )
    }
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        isSynced = isSynced,
        tags = tags.joinToString(","),
        latitude = location?.latitude,
        longitude = location?.longitude,
        address = location?.address,
        accelerometerX = sensorData?.accelerometer?.x,
        accelerometerY = sensorData?.accelerometer?.y,
        accelerometerZ = sensorData?.accelerometer?.z,
        accelerometerTimestamp = sensorData?.accelerometer?.timestamp,
        gyroscopeX = sensorData?.gyroscope?.x,
        gyroscopeY = sensorData?.gyroscope?.y,
        gyroscopeZ = sensorData?.gyroscope?.z,
        gyroscopeTimestamp = sensorData?.gyroscope?.timestamp,
        ambientTemperature = sensorData?.ambient?.temperature,
        ambientHumidity = sensorData?.ambient?.humidity,
        ambientPressure = sensorData?.ambient?.pressure,
        ambientTimestamp = sensorData?.ambient?.timestamp
    )
}

