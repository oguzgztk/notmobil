package com.example.notuygulamasi.data.repository

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import com.example.notuygulamasi.domain.repository.ConnectivityRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Connectivity Repository implementasyonu
 */
@Singleton
class ConnectivityRepositoryImpl @Inject constructor(
    private val context: Context
) : ConnectivityRepository {
    
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    
    private val _wifiConnected = MutableStateFlow<Boolean>(false)
    private val _bluetoothEnabled = MutableStateFlow<Boolean>(false)
    private val _nfcEnabled = MutableStateFlow<Boolean>(false)
    private val _bluetoothDevices = MutableStateFlow<List<String>>(emptyList())
    
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val deviceName = device.name ?: device.address
            val currentDevices = _bluetoothDevices.value.toMutableList()
            if (!currentDevices.contains(deviceName)) {
                currentDevices.add(deviceName)
                _bluetoothDevices.value = currentDevices
            }
        }
        
        override fun onScanFailed(errorCode: Int) {
            // Scan hatası
        }
    }
    
    init {
        updateWifiStatus()
        updateBluetoothStatus()
        updateNfcStatus()
    }
    
    private fun updateWifiStatus() {
        _wifiConnected.value = wifiManager.isWifiEnabled && 
            wifiManager.connectionInfo?.networkId != -1
    }
    
    private fun updateBluetoothStatus() {
        _bluetoothEnabled.value = bluetoothAdapter?.isEnabled == true
    }
    
    private fun updateNfcStatus() {
        _nfcEnabled.value = nfcAdapter?.isEnabled == true
    }
    
    override suspend fun isWifiConnected(): Flow<Boolean> {
        updateWifiStatus()
        return _wifiConnected
    }
    
    override suspend fun isBluetoothEnabled(): Flow<Boolean> {
        updateBluetoothStatus()
        return _bluetoothEnabled
    }
    
    override suspend fun isNfcEnabled(): Flow<Boolean> {
        updateNfcStatus()
        return _nfcEnabled
    }
    
    override suspend fun scanBluetoothDevices(): Flow<List<String>> {
        return callbackFlow {
            _bluetoothDevices.value = emptyList()
            
            if (bluetoothLeScanner != null && bluetoothAdapter?.isEnabled == true) {
                try {
                    bluetoothLeScanner.startScan(scanCallback)
                    
                    // 5 saniye sonra scan'i durdur
                    kotlinx.coroutines.delay(5000)
                    bluetoothLeScanner.stopScan(scanCallback)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
            
            trySend(_bluetoothDevices.value)
            
            awaitClose {
                bluetoothLeScanner?.stopScan(scanCallback)
            }
        }
    }
}

