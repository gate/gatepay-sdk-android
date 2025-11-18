package com.gate.gatepaydemo.data.repository

import com.gate.gatepaydemo.data.model.ApiResponse
import com.gate.gatepaydemo.data.model.SignatureResponse
import com.gate.gatepaydemo.net.HttpServiceAppClient
import com.gate.gatepaydemo.net.OkHttpAppUtils
import com.gateio.sdk.gatepay.app.utils.DemoLogger
import com.google.gson.JsonObject
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Payment Repository - signature retrieval
 */
class PaymentRepository {

    private val signatureApi by lazy { HttpServiceAppClient.getSignatureApi() }

    // Get payment signature (demo only - should be server-side in production)
    fun getPaymentSignature(
        prepayId: String,
        packageExt: String = "GatePay"
    ): Observable<ApiResponse<SignatureResponse.SignatureData>> {
        return Observable.fromCallable {
            val jsonObject = JsonObject().apply {
                addProperty("prepayid", prepayId)
                addProperty("package_ext", packageExt)
            }
            OkHttpAppUtils.getRequestBody(jsonObject)
        }
            .flatMap { body ->
                signatureApi.getPaymentSignature(
                    body = body
                )
            }
            .map { response ->
                val bizData = response.data?.bizData
                val bizCode = response.data?.bizCode

                val isSuccess = (bizCode == null || bizCode == "0" || bizCode.isEmpty()) && bizData != null

                if (isSuccess) {
                    val data = SignatureResponse.SignatureData(
                        prepayId = bizData.prepayId,
                        timestamp = bizData.ts,
                        nonce = bizData.nonce,
                        signature = bizData.signature
                    )
                    ApiResponse.Success(data)
                } else {
                    val errorMsg = response.data?.bizMessage?.takeIf { it.isNotBlank() }
                        ?: response.message?.takeIf { it.isNotBlank() }
                        ?: "Signature retrieval failed (bizCode: $bizCode)"

                    DemoLogger.e("Signature retrieval failed: $errorMsg")

                    ApiResponse.Error(
                        code = bizCode?.toIntOrNull() ?: response.code,
                        message = errorMsg
                    )
                }
            }
            .onErrorReturn { throwable ->
                DemoLogger.e("Signature retrieval exception", throwable)
                ApiResponse.Exception(throwable)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .startWithItem(ApiResponse.Loading)
    }

}

