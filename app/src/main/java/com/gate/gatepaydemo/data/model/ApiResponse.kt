package com.gate.gatepaydemo.data.model

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

/** Create Order Response */
data class CreateOrderResponse(
    /** Response code, "000000" when successful */
    @SerializedName("code")
    val code: String? = null,

    /** Status, "SUCCESS" when successful */
    @SerializedName("status")
    val status: String? = null,

    /** Error message */
    @SerializedName("errorMessage")
    val errorMessage: String? = null,

    /** Order data */
    @SerializedName("data")
    val data: OrderData? = null
) {
    data class OrderData(
        @SerializedName("prepayId")
        val prepayId: String? = null,

        @SerializedName("terminalType")
        val terminalType: String? = null,

        @SerializedName("expireTime")
        val expireTime: Long? = null,

        @SerializedName("qrContent")
        val qrContent: String? = null,

        @SerializedName("location")
        val location: String? = null,

        @SerializedName("payCurrency")
        val payCurrency: String? = null,

        @SerializedName("payAmount")
        val payAmount: String? = null
    )

    /** Check if successful: code="000000" or status="SUCCESS" and prepayId not empty */
    fun isSuccess(): Boolean {
        return (code == "000000" || status == "SUCCESS") && !prepayId.isNullOrEmpty()
    }

    val prepayId: String?
        get() = data?.prepayId
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

