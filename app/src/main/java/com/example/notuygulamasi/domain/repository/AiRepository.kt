package com.example.notuygulamasi.domain.repository

/**
 * AI repository interface
 */
interface AiRepository {
    suspend fun summarizeText(text: String): Result<String>
    suspend fun generateTags(text: String): Result<List<String>>
    suspend fun classifyText(text: String): Result<String>
}

