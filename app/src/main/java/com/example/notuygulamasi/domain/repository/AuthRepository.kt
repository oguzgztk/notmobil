package com.example.notuygulamasi.domain.repository

import com.example.notuygulamasi.domain.model.User

/**
 * Authentication repository interface
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
    suspend fun refreshToken(): Result<String>
    suspend fun getCurrentUser(): User?
    suspend fun isLoggedIn(): Boolean
}

