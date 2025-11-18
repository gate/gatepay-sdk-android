package com.gate.gatepaydemo.net

import android.util.Log
import com.gate.gatepaydemo.config.DemoConfig
import com.gateio.sdk.gatepay.app.utils.DemoLogger
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * OkHttp Utility
 * 
 * Provides singleton OkHttpClient with:
 * - Timeout configuration
 * - Logging interceptor
 * - Request/Response interceptor
 * 
 * ⚠️ Note: Uses unsafe SSL config in testing (bypasses certificate validation)
 * Production must use proper SSL certificate validation
 */
object OkHttpAppUtils {
    
    private const val TAG = "OkHttpAppUtils"

    @Volatile
    private var singleton: OkHttpClient? = null

    /**
     * Get OkHttpClient singleton instance
     */
    fun getInstance(): OkHttpClient {
        return singleton ?: synchronized(this) {
            singleton ?: createOkHttpClient().also { singleton = it }
        }
    }

    /**
     * Create OkHttpClient instance
     */
    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(DemoConfig.NETWORK_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(DemoConfig.NETWORK_READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(DemoConfig.NETWORK_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
        
        // ⚠️ Testing only: Bypass SSL certificate validation
        // Remove in production, use system default certificate validation
        if (DemoConfig.ENABLE_DEBUG) {
            builder.sslSocketFactory(getUnsafeSslSocketFactory(), getTrustAllManager())
            builder.hostnameVerifier { _, _ -> true }
            Log.w(TAG, "⚠️ Using unsafe SSL config (testing only)")
        }

        // Add logging interceptor
        if (DemoConfig.ENABLE_DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.d("OkHttp", message)
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        // Add custom logging interceptor
        builder.addInterceptor(createLoggingInterceptor())
        
        return builder.build()
    }

    /**
     * Create logging interceptor for detailed request/response info
     */
    private fun createLoggingInterceptor(): okhttp3.Interceptor {
        return okhttp3.Interceptor { chain ->
            val request = chain.request()
            
            if (DemoConfig.ENABLE_DEBUG) {
                // Log request info
                DemoLogger.d("========================================")
                DemoLogger.d("Request: ${request.method} ${request.url}")
                DemoLogger.d("----------------------------------------")
                DemoLogger.d("Headers:")
                request.headers.forEach { (name, value) ->
                    DemoLogger.d("  $name: $value")
                }
                
                // Log request body info
                request.body?.let { body ->
                    DemoLogger.d("Body Content-Type: ${body.contentType()}")
                    DemoLogger.d("Body Size: ${body.contentLength()} bytes")
                }
            }
            
            val response = chain.proceed(request)
            
            if (DemoConfig.ENABLE_DEBUG) {
                // Log response info
                DemoLogger.d("----------------------------------------")
                DemoLogger.d("Response: HTTP ${response.code} ${response.message}")
                DemoLogger.d("Headers:")
                response.headers.forEach { (name, value) ->
                    DemoLogger.d("  $name: $value")
                }
                
                // Check response body length
                val contentLength = response.body?.contentLength() ?: -1
                DemoLogger.d("Body Length: $contentLength bytes")
                
                if (contentLength == 0L) {
                    DemoLogger.d("⚠️ Warning: Empty response body!")
                }
                DemoLogger.d("========================================")
            }
            
            response
        }
    }

    /**
     * Convert object to JSON RequestBody
     */
    fun getRequestBody(params: Any): RequestBody {
        val json = Gson().toJson(params)
        return json.toRequestBody("application/json".toMediaType())
    }

    /**
     * Create unsafe SSLSocketFactory
     * ⚠️ Testing only, DO NOT use in production
     */
    private fun getUnsafeSslSocketFactory(): SSLSocketFactory {
        return try {
            val trustAllCerts = arrayOf<TrustManager>(getTrustAllManager())
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException("Failed to create SSLSocketFactory", e)
        }
    }

    /**
     * Create TrustManager that trusts all certificates
     * ⚠️ Testing only, DO NOT use in production
     */
    private fun getTrustAllManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {
                // No client certificate validation
            }

            override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {
                // No server certificate validation
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }
}
