package com.example.notuygulamasi.data.remote.dto

/**
 * AI Service DTOs
 */
data class SummarizeRequest(
    val text: String,
    val geminiApiKey: String? = null
)

data class SummarizeResponse(
    val summary: String
)

data class GenerateTagsRequest(
    val text: String
)

data class GenerateTagsResponse(
    val tags: List<String>
)

data class ClassifyRequest(
    val text: String
)

data class ClassifyResponse(
    val category: String
)

