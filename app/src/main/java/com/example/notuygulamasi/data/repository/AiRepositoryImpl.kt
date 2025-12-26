package com.example.notuygulamasi.data.repository

import com.example.notuygulamasi.data.remote.api.AiApiService
import com.example.notuygulamasi.data.remote.dto.*
import com.example.notuygulamasi.domain.repository.AiRepository
import com.example.notuygulamasi.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * AI Repository implementasyonu
 */
class AiRepositoryImpl @Inject constructor(
    private val aiApiService: AiApiService,
    private val settingsRepository: SettingsRepository
) : AiRepository {
    
    override suspend fun summarizeText(text: String): Result<String> {
        return try {
            // API key'i al
            val apiKey = settingsRepository.getGeminiApiKey().first()
            val request = SummarizeRequest(text, apiKey)
            val response = aiApiService.summarize(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.summary)
            } else {
                Result.failure(Exception("Summarization failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun generateTags(text: String): Result<List<String>> {
        return try {
            val request = GenerateTagsRequest(text)
            val response = aiApiService.generateTags(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.tags)
            } else {
                Result.failure(Exception("Tag generation failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun classifyText(text: String): Result<String> {
        return try {
            val request = ClassifyRequest(text)
            val response = aiApiService.classify(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.category)
            } else {
                Result.failure(Exception("Classification failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

