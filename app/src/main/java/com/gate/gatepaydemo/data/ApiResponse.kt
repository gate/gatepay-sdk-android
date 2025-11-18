package com.gate.gatepaydemo.data

import com.google.gson.annotations.SerializedName

/**
 * Unified API Response Model
 *
 * Wraps all API request results with unified error handling
 */
sealed class ApiResponse<out T> {
    /** Request successful */
    data class Success<T>(val data: T) : ApiResponse<T>()

    /** Request failed */
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>()

    /** Network exception */
    data class Exception(val throwable: Throwable) : ApiResponse<Nothing>()

    /** Loading state */
    object Loading : ApiResponse<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isException(): Boolean = this is Exception
    fun getOrNull(): T? = if (this is Success) data else null
}

/** Signature Response Data */
data class SignatureResponse(
    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: SignatureDataWrapper? = null
) {
    data class SignatureDataWrapper(
        @SerializedName("bizCode")
        val bizCode: String? = null,

        @SerializedName("bizMessage")
        val bizMessage: String? = null,

        @SerializedName("bizData")
        val bizData: SignatureData? = null
    )

    data class SignatureData(
        @SerializedName("prepayId")
        val prepayId: String? = null,

        @SerializedName("ts")
        val timestamp: Long = 0L,

        @SerializedName("nonce")
        val nonce: String? = null,

        @SerializedName("signature")
        val signature: String? = null
    )

    fun isSuccess(): Boolean {
        return code == 0 && data?.bizData != null
    }

    val signatureData: SignatureData?
        get() = data?.bizData
}

