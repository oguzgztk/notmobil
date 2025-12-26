package com.example.notuygulamasi.domain.repository

import com.example.notuygulamasi.domain.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Not repository interface
 */
interface NoteRepository {
    suspend fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: String): Note?
    suspend fun searchNotes(query: String): Flow<List<Note>>
    suspend fun filterNotesByTag(tag: String): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun syncNotes()
}

