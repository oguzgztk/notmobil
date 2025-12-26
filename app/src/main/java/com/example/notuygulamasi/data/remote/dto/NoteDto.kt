package com.example.notuygulamasi.data.remote.dto

import com.example.notuygulamasi.domain.model.Note
import java.util.Date

/**
 * API için Note DTO
 */
data class NoteDto(
    val id: String? = null,
    val title: String,
    val content: String,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
    val isSynced: Boolean? = null,
    val tags: List<String>? = null,
    val location: LocationDto? = null,
    val sensorData: SensorDataDto? = null
)

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val address: String? = null
)

data class SensorDataDto(
    val accelerometer: AccelerometerDto? = null,
    val gyroscope: GyroscopeDto? = null,
    val ambient: AmbientDto? = null
)

data class AccelerometerDto(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class GyroscopeDto(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
)

data class AmbientDto(
    val temperature: Float? = null,
    val humidity: Float? = null,
    val pressure: Float? = null,
    val timestamp: Long
)

fun NoteDto.toDomain(): Note {
    return Note(
        id = id ?: "",
        title = title,
        content = content,
        createdAt = createdAt?.let { Date(it) } ?: Date(),
        updatedAt = updatedAt?.let { Date(it) } ?: Date(),
        isSynced = isSynced ?: false,
        tags = tags ?: emptyList(),
        location = location?.let {
            com.example.notuygulamasi.domain.model.LocationData(
                latitude = it.latitude,
                longitude = it.longitude,
                address = it.address
            )
        },
        sensorData = sensorData?.let {
            com.example.notuygulamasi.domain.model.SensorData(
                accelerometer = it.accelerometer?.let { acc ->
                    com.example.notuygulamasi.domain.model.AccelerometerData(
                        x = acc.x,
                        y = acc.y,
                        z = acc.z,
                        timestamp = acc.timestamp
                    )
                },
                gyroscope = it.gyroscope?.let { gyro ->
                    com.example.notuygulamasi.domain.model.GyroscopeData(
                        x = gyro.x,
                        y = gyro.y,
                        z = gyro.z,
                        timestamp = gyro.timestamp
                    )
                },
                ambient = it.ambient?.let { amb ->
                    com.example.notuygulamasi.domain.model.AmbientData(
                        temperature = amb.temperature,
                        humidity = amb.humidity,
                        pressure = amb.pressure,
                        timestamp = amb.timestamp
                    )
                }
            )
        }
    )
}

fun Note.toDto(): NoteDto {
    return NoteDto(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt.time,
        updatedAt = updatedAt.time,
        isSynced = isSynced,
        tags = tags,
        location = location?.let {
            LocationDto(
                latitude = it.latitude,
                longitude = it.longitude,
                address = it.address
            )
        },
        sensorData = sensorData?.let {
            SensorDataDto(
                accelerometer = it.accelerometer?.let { acc ->
                    AccelerometerDto(
                        x = acc.x,
                        y = acc.y,
                        z = acc.z,
                        timestamp = acc.timestamp
                    )
                },
                gyroscope = it.gyroscope?.let { gyro ->
                    GyroscopeDto(
                        x = gyro.x,
                        y = gyro.y,
                        z = gyro.z,
                        timestamp = gyro.timestamp
                    )
                },
                ambient = it.ambient?.let { amb ->
                    AmbientDto(
                        temperature = amb.temperature,
                        humidity = amb.humidity,
                        pressure = amb.pressure,
                        timestamp = amb.timestamp
                    )
                }
            )
        }
    )
}

