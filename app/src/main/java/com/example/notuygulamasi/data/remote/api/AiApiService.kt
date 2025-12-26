package com.example.notuygulamasi.data.remote.api

import com.example.notuygulamasi.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AI API servisi
 */
interface AiApiService {
    @POST("ai/summarize")
    suspend fun summarize(@Body request: SummarizeRequest): Response<SummarizeResponse>
    
    @POST("ai/generate-tags")
    suspend fun generateTags(@Body request: GenerateTagsRequest): Response<GenerateTagsResponse>
    
    @POST("ai/classify")
    suspend fun classify(@Body request: ClassifyRequest): Response<ClassifyResponse>
}

