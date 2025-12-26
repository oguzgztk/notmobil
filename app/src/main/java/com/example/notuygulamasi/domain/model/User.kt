package com.example.notuygulamasi.domain.model

/**
 * Kullanıcı domain model
 */
data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

