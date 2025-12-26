package com.example.notuygulamasi.data.remote.interceptor

import com.example.notuygulamasi.data.local.datastore.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * JWT Token yönetimi için OkHttp Interceptor
 */
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Token'ı al (blocking call - sadece interceptor için)
        val token = runBlocking {
            preferencesManager.getAccessToken().first()
        }
        
        val requestBuilder = originalRequest.newBuilder()
        
        // Token varsa header'a ekle
        token?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        requestBuilder.addHeader("Content-Type", "application/json")
        
        return chain.proceed(requestBuilder.build())
    }
}

