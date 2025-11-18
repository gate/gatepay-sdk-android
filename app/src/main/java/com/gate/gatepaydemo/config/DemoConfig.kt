package com.gate.gatepaydemo.config

/**
 * Demo Configuration Manager
 *
 * Centralized configuration for the demo application:
 * - SDK initialization settings
 * - Merchant server configuration
 * - Network request settings
 *
 * ⚠️ Warning: For demonstration purposes only
 * In production environments:
 * 1. Fetch configuration from secure servers
 * 2. Never hardcode sensitive information on client side
 * 3. Use environment variables or secure storage for keys
 */
object DemoConfig {

    /**
     * Enable Debug
     * Recommended: enabled in debug, disabled in release
     */
    const val ENABLE_DEBUG = false

    /**
     * SDK Client ID
     */
    internal var SDK_CLIENT_ID = "xxxxxx"

    /**
     * Application name
     */
    const val APP_NAME = "GatePay Demo"

    /**
     * Application icon URL
     */
    const val APP_ICON_URL = "https://xxxxxx"

    // ==================== Merchant Server Configuration ====================

    /**
     * SDK signature server base URL
     * For testing environment only
     */
    const val SIGNATURE_BASE_URL = "https://xxxxxx/"

    // ==================== Network Configuration ====================

    /**
     * Network connect timeout (milliseconds)
     */
    const val NETWORK_CONNECT_TIMEOUT = 15000L

    /**
     * Network read timeout (milliseconds)
     */
    const val NETWORK_READ_TIMEOUT = 20000L

    /**
     * Network write timeout (milliseconds)
     */
    const val NETWORK_WRITE_TIMEOUT = 15000L

}
