package com.example.notuygulamasi.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notuygulamasi.MainActivity
import com.example.notuygulamasi.domain.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Bildirim göndermek için WorkManager Worker
 */
@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val noteRepository: NoteRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Not sayısını kontrol et - Flow'dan ilk değeri al
            val notes = noteRepository.getAllNotes().first()
            val noteCount = notes.size
            
            // Bildirim gönder
            sendNotification(applicationContext, noteCount)
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun sendNotification(context: Context, noteCount: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = "not_mobil_channel"
        val channel = NotificationChannel(
            channelId,
            "Not Mobil Bildirimleri",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
        
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Not Mobil")
            .setContentText("Toplam $noteCount notunuz var")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1, notification)
    }
}

