package com.gate.gatepaydemo.net

import com.gate.gatepaydemo.bean.SigDataBean
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * API Service Interface
 * 
 * Defines merchant backend APIs:
 * - Create payment order
 * - Get payment signature
 */
interface InterfaceAppService {

    /**
     * Get payment signature
     * @param body Signature request data
     */
    @Headers("Content-Type: application/json")
    @POST("xxxxxx")
    fun getPaymentSignature(
        @Body body: RequestBody
    ): Observable<SigDataBean>
}
