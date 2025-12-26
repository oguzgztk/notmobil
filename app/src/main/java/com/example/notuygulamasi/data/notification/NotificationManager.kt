package com.example.notuygulamasi.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.notuygulamasi.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bildirim yönetimi
 */
@Singleton
class NotificationManager @Inject constructor(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Not Mobil Bildirimleri",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Not uygulaması bildirimleri"
        }
        notificationManager.createNotificationChannel(channel)
    }
    
    fun showNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID++, notification)
    }
    
    companion object {
        private const val CHANNEL_ID = "not_mobil_channel"
        private var NOTIFICATION_ID = 1
    }
}

