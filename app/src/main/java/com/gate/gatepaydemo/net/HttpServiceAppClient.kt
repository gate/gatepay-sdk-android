package com.gate.gatepaydemo.net

import com.gate.gatepaydemo.config.DemoConfig

/**
 * HTTP Service Client
 * 
 * Provides unified API interface instances
 */
object HttpServiceAppClient {

    /**
     * Get signature API instance
     * For calling signature service (testing environment only)
     */
    fun getSignatureApi(): InterfaceAppService {
        return RetrofitAppUtils.createApi(
            InterfaceAppService::class.java,
            DemoConfig.SIGNATURE_BASE_URL
        )
    }
}
