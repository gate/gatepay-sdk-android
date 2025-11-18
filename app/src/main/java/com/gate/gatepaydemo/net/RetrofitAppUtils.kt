package com.gate.gatepaydemo.net

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit Utility
 * 
 * Creates and manages Retrofit instances with:
 * - Gson converter
 * - RxJava3 adapter
 * - Dynamic base URL support
 */
object RetrofitAppUtils {
    
    /**
     * Gson instance with lenient parsing and null serialization
     */
    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .serializeNulls()
            .create()
    }

    /**
     * Create Retrofit API instance
     * 
     * @param clazz API interface class
     * @param baseUrl Base URL
     */
    fun <T> createApi(clazz: Class<T>, baseUrl: String): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpAppUtils.getInstance())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        
        return retrofit.create(clazz)
    }
}
