package com.example.notuygulamasi.data.repository

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.notuygulamasi.data.local.datastore.PreferencesManager
import com.example.notuygulamasi.data.remote.api.AuthApiService
import com.example.notuygulamasi.data.remote.dto.LoginRequest
import com.example.notuygulamasi.data.remote.dto.RefreshTokenRequest
import com.example.notuygulamasi.domain.model.User
import com.example.notuygulamasi.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Authentication Repository implementasyonu
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val preferencesManager: PreferencesManager
) : AuthRepository {
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApiService.login(request)
            
            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                
                // Token'ları kaydet
                preferencesManager.saveAccessToken(loginResponse.accessToken)
                preferencesManager.saveRefreshToken(loginResponse.refreshToken)
                preferencesManager.saveUserInfo(
                    loginResponse.user.id,
                    loginResponse.user.email
                )
                
                val user = User(
                    id = loginResponse.user.id,
                    email = loginResponse.user.email,
                    name = loginResponse.user.name,
                    accessToken = loginResponse.accessToken,
                    refreshToken = loginResponse.refreshToken
                )
                
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun logout() {
        preferencesManager.clearAll()
    }
    
    override suspend fun refreshToken(): Result<String> {
        return try {
            val refreshToken = preferencesManager.getRefreshToken().first()
                ?: return Result.failure(Exception("No refresh token available"))
            
            val request = RefreshTokenRequest(refreshToken)
            val response = authApiService.refreshToken(request)
            
            if (response.isSuccessful && response.body() != null) {
                val newAccessToken = response.body()!!.accessToken
                preferencesManager.saveAccessToken(newAccessToken)
                Result.success(newAccessToken)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        val userId = preferencesManager.getUserId().first() ?: return null
        val email = preferencesManager.getUserEmail().first() ?: return null
        val accessToken = preferencesManager.getAccessToken().first()
        val refreshToken = preferencesManager.getRefreshToken().first()
        
        return User(
            id = userId,
            email = email,
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
    
    override suspend fun isLoggedIn(): Boolean {
        val token = preferencesManager.getAccessToken().first()
        if (token == null) return false
        
        // JWT token'ı decode et ve expire kontrolü yap
        return try {
            val jwt = JWT.decode(token)
            val expiresAt = jwt.expiresAt
            expiresAt?.after(java.util.Date()) ?: false
        } catch (e: Exception) {
            false
        }
    }
}

