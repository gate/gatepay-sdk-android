package com.gateio.sdk.gatepay.app.utils

import android.util.Log
import com.gate.gatepaydemo.config.DemoConfig

/**
 * Demo Application Logger
 * 
 * Centralized logging utility for the demo app.
 * 
 * Features:
 * - Only logs errors and critical information
 * - Respects debug environment settings
 * - Consistent log tags
 * - Easy to disable in production
 * 
 * Usage:
 * ```
 * DemoLogger.e("Error occurred", exception)
 * DemoLogger.w("Warning message")
 * ```
 */
object DemoLogger {
    
    private const val TAG = "GatePayDemo"
    
    /**
     * Log an error message
     * Used for exceptions and critical errors
     */
    fun e(message: String, throwable: Throwable? = null) {
        if (DemoConfig.ENABLE_DEBUG) {
            if (throwable != null) {
                Log.e(TAG, message, throwable)
            } else {
                Log.e(TAG, message)
            }
        }
    }
    
    /**
     * Log a warning message
     * Used for non-critical issues
     */
    fun w(message: String) {
        if (DemoConfig.ENABLE_DEBUG) {
            Log.w(TAG, message)
        }
    }
    
    /**
     * Log debug information
     * Only enabled in debug builds
     */
    fun d(message: String) {
        if (DemoConfig.ENABLE_DEBUG) {
            Log.d(TAG, message)
        }
    }
}
