package com.example.notuygulamasi.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.notuygulamasi.data.work.NotificationWorker
import com.example.notuygulamasi.data.work.SyncWorker
import java.util.concurrent.TimeUnit

/**
 * Boot completed broadcast receiver
 * Cihaz açıldığında periyodik görevleri başlatır
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val workManager = WorkManager.getInstance(context)
            
            // Periyodik senkronizasyon işini başlat
            val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.MINUTES
            ).build()
            
            workManager.enqueueUniquePeriodicWork(
                "sync_work",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
            )
            
            // Periyodik bildirim işini başlat
            val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.HOURS
            ).build()
            
            workManager.enqueueUniquePeriodicWork(
                "notification_work",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationWorkRequest
            )
        }
    }
}

