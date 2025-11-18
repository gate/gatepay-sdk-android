package com.gate.gatepaydemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gate.gatepaydemo.data.model.ApiResponse
import com.gate.gatepaydemo.data.model.SignatureResponse
import com.gate.gatepaydemo.data.repository.PaymentRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

/**
 * ViewModel for MainActivity
 * Handles payment signature retrieval
 */
class MainViewModel : ViewModel() {

    private val repository = PaymentRepository()
    private val compositeDisposable = CompositeDisposable()

    private val _signatureResult = MutableLiveData<ApiResponse<SignatureResponse.SignatureData>>()
    val signatureResult: LiveData<ApiResponse<SignatureResponse.SignatureData>> = _signatureResult

    private val _inputError = MutableLiveData<String?>()
    val inputError: LiveData<String?> = _inputError

    private val _paymentLog = MutableLiveData<String>()
    val paymentLog: LiveData<String> = _paymentLog

    fun getPaymentSignature(prepayId: String, packageExt: String = "GatePay") {
        if (prepayId.isBlank()) {
            _inputError.value = "Please enter prepay ID"
            return
        }

        val disposable = repository.getPaymentSignature(prepayId, packageExt)
            .subscribe(
                { response -> _signatureResult.value = response },
                { throwable -> _signatureResult.value = ApiResponse.Exception(throwable) }
            )

        compositeDisposable.add(disposable)
    }

    fun clearInputError() {
        _inputError.value = null
    }

    fun updatePaymentLog(log: String) {
        _paymentLog.value = log
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
