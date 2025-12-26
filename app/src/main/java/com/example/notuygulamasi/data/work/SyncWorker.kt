package com.example.notuygulamasi.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.notuygulamasi.domain.repository.NoteRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Periyodik senkronizasyon için WorkManager Worker
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val noteRepository: NoteRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Notları senkronize et
            noteRepository.syncNotes()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

