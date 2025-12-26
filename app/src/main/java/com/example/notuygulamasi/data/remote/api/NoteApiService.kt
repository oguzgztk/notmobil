package com.example.notuygulamasi.data.remote.api

import com.example.notuygulamasi.data.remote.dto.NoteDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Not API servisi
 */
interface NoteApiService {
    @GET("notes")
    suspend fun getAllNotes(): Response<List<NoteDto>>
    
    @GET("notes/{id}")
    suspend fun getNoteById(@Path("id") id: String): Response<NoteDto>
    
    @POST("notes")
    suspend fun createNote(@Body note: NoteDto): Response<NoteDto>
    
    @PUT("notes/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body note: NoteDto): Response<NoteDto>
    
    @DELETE("notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Unit>
}

