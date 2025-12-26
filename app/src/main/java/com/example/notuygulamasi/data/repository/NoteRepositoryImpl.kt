package com.example.notuygulamasi.data.repository

import com.example.notuygulamasi.data.local.dao.NoteDao
import com.example.notuygulamasi.data.local.entity.toEntity
import com.example.notuygulamasi.data.remote.api.NoteApiService
import com.example.notuygulamasi.data.remote.dto.toDomain
import com.example.notuygulamasi.data.remote.dto.toDto
import com.example.notuygulamasi.domain.model.Note
import com.example.notuygulamasi.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Not Repository implementasyonu
 */
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val noteApiService: NoteApiService
) : NoteRepository {
    
    override suspend fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getNoteById(id: String): Note? {
        return noteDao.getNoteById(id)?.toDomain()
    }
    
    override suspend fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun filterNotesByTag(tag: String): Flow<List<Note>> {
        return noteDao.filterNotesByTag(tag).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun insertNote(note: Note) {
        noteDao.insertNote(note.toEntity())
    }
    
    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note.toEntity())
    }
    
    override suspend fun deleteNote(id: String) {
        noteDao.deleteNoteById(id)
    }
    
    override suspend fun syncNotes() {
        try {
            // Senkronize edilmemiş notları al
            val unsyncedNotes = noteDao.getUnsyncedNotes()
            
            for (entity in unsyncedNotes) {
                val note = entity.toDomain()
                val noteDto = note.toDto()
                
                try {
                    if (note.id.isEmpty() || !note.isSynced) {
                        // Yeni not oluştur
                        val response = noteApiService.createNote(noteDto)
                        if (response.isSuccessful) {
                            response.body()?.let { createdNote ->
                                val syncedNote = createdNote.toDomain()
                                noteDao.insertNote(syncedNote.toEntity().copy(isSynced = true))
                            }
                        }
                    } else {
                        // Mevcut notu güncelle
                        val response = noteApiService.updateNote(note.id, noteDto)
                        if (response.isSuccessful) {
                            noteDao.updateNote(entity.copy(isSynced = true))
                        }
                    }
                } catch (e: Exception) {
                    // Hata durumunda yerel veritabanında kalır, sonra tekrar denenecek
                    e.printStackTrace()
                }
            }
            
            // Sunucudan tüm notları çek ve yerel veritabanını güncelle
            val response = noteApiService.getAllNotes()
            if (response.isSuccessful) {
                response.body()?.let { notes ->
                    notes.forEach { noteDto ->
                        val note = noteDto.toDomain()
                        noteDao.insertNote(note.toEntity().copy(isSynced = true))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

