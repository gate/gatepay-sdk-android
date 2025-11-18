package com.gate.gatepaydemo.bean

import com.google.gson.annotations.SerializedName

data class SigDataBean(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("label")
    val label: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("pageSize")
    val pageSize: Int = 0,
    @SerializedName("pageCount")
    val pageCount: Int = 0,
    @SerializedName("totalCount")
    val totalCount: Int = 0,
    @SerializedName("data")
    val data: DataBean? = null
) {
    data class DataBean(
        @SerializedName("bizCode")
        val bizCode: String? = null,
        @SerializedName("bizData")
        val bizData: BizDataBean? = null,
        @SerializedName("bizMessage")
        val bizMessage: String? = null
    ) {
        data class BizDataBean(
            @SerializedName("prepayId")
            val prepayId: String? = null,
            @SerializedName("ts")
            val ts: Long = 0L,
            @SerializedName("nonce")
            val nonce: String? = null,
            @SerializedName("signature")
            val signature: String? = null
        )
    }
}