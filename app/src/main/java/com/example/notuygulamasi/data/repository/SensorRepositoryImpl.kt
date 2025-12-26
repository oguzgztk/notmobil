package com.example.notuygulamasi.data.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import com.example.notuygulamasi.domain.model.AccelerometerData
import com.example.notuygulamasi.domain.model.AmbientData
import com.example.notuygulamasi.domain.model.GyroscopeData
import com.example.notuygulamasi.domain.model.LocationData
import com.example.notuygulamasi.domain.model.SensorData
import com.example.notuygulamasi.domain.repository.SensorRepository
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sensor Repository implementasyonu
 */
@Singleton
class SensorRepositoryImpl @Inject constructor(
    private val context: Context
) : SensorRepository {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10000L // 10 saniye
    ).build()
    
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                _currentLocation.value = LocationData(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            }
        }
    }
    
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val ambientTempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
    private val humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
    private val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    
    private val _sensorData = MutableStateFlow<SensorData>(SensorData())
    
    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val current = _sensorData.value
                    _sensorData.value = current.copy(
                        accelerometer = AccelerometerData(
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2],
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                Sensor.TYPE_GYROSCOPE -> {
                    val current = _sensorData.value
                    _sensorData.value = current.copy(
                        gyroscope = GyroscopeData(
                            x = event.values[0],
                            y = event.values[1],
                            z = event.values[2],
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    val current = _sensorData.value
                    val ambient = current.ambient
                    _sensorData.value = current.copy(
                        ambient = AmbientData(
                            temperature = event.values[0],
                            humidity = ambient?.humidity,
                            pressure = ambient?.pressure,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    val current = _sensorData.value
                    val ambient = current.ambient
                    _sensorData.value = current.copy(
                        ambient = AmbientData(
                            temperature = ambient?.temperature,
                            humidity = event.values[0],
                            pressure = ambient?.pressure,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                Sensor.TYPE_PRESSURE -> {
                    val current = _sensorData.value
                    val ambient = current.ambient
                    _sensorData.value = current.copy(
                        ambient = AmbientData(
                            temperature = ambient?.temperature,
                            humidity = ambient?.humidity,
                            pressure = event.values[0],
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Accuracy değişikliği için gerekirse işlem yapılabilir
        }
    }
    
    override suspend fun getCurrentLocation(): Flow<LocationData?> {
        return _currentLocation
    }
    
    /**
     * Mevcut konumu direkt olarak al (tek seferlik)
     */
    suspend fun getCurrentLocationOnce(): LocationData? {
        return try {
            // Önce konum güncellemelerini başlat
            startLocationUpdates()
            // Kısa bir süre bekle
            kotlinx.coroutines.delay(1000)
            // Son konumu al
            _currentLocation.value
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getSensorData(): Flow<SensorData> {
        return _sensorData
    }
    
    override suspend fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    
    override suspend fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    
    override suspend fun startSensorUpdates() {
        accelerometerSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscopeSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        ambientTempSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        humiditySensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        pressureSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
    
    override suspend fun stopSensorUpdates() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}

