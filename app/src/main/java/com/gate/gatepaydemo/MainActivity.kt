package com.gate.gatepaydemo

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.gate.gatepaydemo.data.model.ApiResponse
import com.gate.gatepaydemo.data.model.SignatureResponse
import com.gateio.sdk.gatepay.app.utils.DemoLogger
import com.gateio.sdk.gatepay.base.GatePaySDK
import com.gateio.sdk.gatepay.callback.OpenGatePayListener
import kotlin.getValue

class MainActivity  : AppCompatActivity() {

    companion object {
        private const val EXTRA_PREPAY_ID = "prepayId"
        private const val STATE_PREPAY_ID = "state_prepay_id"
        private const val STATE_LOG_TEXT = "state_log_text"
    }

    private val viewModel: MainViewModel by viewModels()

    // UI components
    private lateinit var etPrepayId: EditText
    private lateinit var tvLog: TextView
    private lateinit var btnOpenGate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)

        initViews()
        restoreState(savedInstanceState)
        handleIntent()
        setupListeners()
        observeViewModel()
    }

    /**
     * Restore saved state
     */
    private fun restoreState(savedInstanceState: Bundle?) {
        when {
            // Priority 1: Restore from savedInstanceState (normal recreation)
            savedInstanceState != null -> {
                val prepayId = savedInstanceState.getString(STATE_PREPAY_ID)
                val logText = savedInstanceState.getString(STATE_LOG_TEXT)

                if (!prepayId.isNullOrEmpty()) {
                    etPrepayId.setText(prepayId)
                }
                if (!logText.isNullOrEmpty()) {
                    tvLog.text = logText
                }
            }
            // Priority 2: Restore from Intent extras (theme change recreation)
            intent?.hasExtra(STATE_PREPAY_ID) == true -> {
                val prepayId = intent.getStringExtra(STATE_PREPAY_ID)
                val logText = intent.getStringExtra(STATE_LOG_TEXT)

                if (!prepayId.isNullOrEmpty()) {
                    etPrepayId.setText(prepayId)
                }
                if (!logText.isNullOrEmpty()) {
                    tvLog.text = logText
                }
            }
        }
    }

    /**
     * Initialize UI components
     */
    private fun initViews() {
        tvLog = findViewById(R.id.tv_input_log)
        etPrepayId = findViewById(R.id.et_prepay_id)
        btnOpenGate = findViewById(R.id.bt_open_gate)
    }

    /**
     * Handle Intent parameters
     */
    private fun handleIntent() {
        val prepayId = intent?.getStringExtra(EXTRA_PREPAY_ID)
        if (!prepayId.isNullOrEmpty()) {
            etPrepayId.setText(prepayId)
            DemoLogger.d("Received prepayId: $prepayId")
        }
    }

    /**
     * Setup listeners
     */
    private fun setupListeners() {
        // Open payment button
        btnOpenGate.setOnClickListener {
            val prepayId = etPrepayId.text.toString().trim()
            if (prepayId.isEmpty()) {
                Toast.makeText(this, R.string.toast_enter_prepay_id, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.getPaymentSignature(prepayId, "GatePay")
        }

        // UI customization buttons
        findViewById<Button>(R.id.btn_demo_switch_language)?.setOnClickListener {
            DemoSwitchers.showLanguagePicker(this)
        }

        findViewById<Button>(R.id.btn_demo_switch_theme_color)?.setOnClickListener {
            DemoSwitchers.showThemeColorPicker(this)
        }

        findViewById<Button>(R.id.btn_demo_switch_day_night)?.setOnClickListener {
            DemoSwitchers.showDayNightPicker(this)
        }
    }

    /**
     * Observe ViewModel data changes
     */
    private fun observeViewModel() {
        // Observe signature retrieval result
        viewModel.signatureResult.observe(this) { response ->
            handleSignatureResponse(response)
        }

        // Observe input validation error
        viewModel.inputError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe payment log
        viewModel.paymentLog.observe(this) { log ->
            updateLog(log)
        }
    }

    /**
     * Handle signature response
     */
    private fun handleSignatureResponse(response: ApiResponse<SignatureResponse.SignatureData>) {
        when (response) {
            is ApiResponse.Loading -> {
                btnOpenGate.isEnabled = false
                btnOpenGate.text = "Loading..."
                updateLog("Retrieving payment signature...")
            }

            is ApiResponse.Success -> {
                btnOpenGate.isEnabled = true
                btnOpenGate.text = getString(R.string.btn_open_cashier)

                val signatureData = response.data
                updateLog("Signature retrieved, opening payment...")

                openGatePay(signatureData)
            }

            is ApiResponse.Error -> {
                btnOpenGate.isEnabled = true
                btnOpenGate.text = getString(R.string.btn_open_cashier)

                val errorMessage = getString(R.string.toast_signature_failed, response.message)
                updateLog(errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }

            is ApiResponse.Exception -> {
                btnOpenGate.isEnabled = true
                btnOpenGate.text = getString(R.string.btn_open_cashier)

                val errorMessage = getString(
                    R.string.toast_network_error,
                    response.throwable.message ?: "Unknown error"
                )
                updateLog(errorMessage)
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Open GatePay payment
     *
     * @param signatureData Signature data
     */
    private fun openGatePay(signatureData: SignatureResponse.SignatureData) {
        DemoLogger.d("Opening GatePay SDK")

        GatePaySDK.openGatePay(
            activity = this@MainActivity,
            signature = signatureData.signature ?: "",
            timestamp = "${signatureData.timestamp}",
            nonce = signatureData.nonce ?: "",
            prepayId = signatureData.prepayId ?: "",
            packageExt = "GatePay",
            openGatePayListener = object : OpenGatePayListener {
                override fun onGateOpenSuccess() {
                    DemoLogger.d("GatePay SDK opened successfully")
                    updateLog(getString(R.string.log_sdk_open_success))
                }

                override fun onGateOpenFailed(code: Int, errorMessage: String?) {
                    val message = getString(R.string.log_sdk_open_failed, code, errorMessage ?: "")
                    DemoLogger.e("GatePay SDK open failed: code=$code, message=$errorMessage")
                    updateLog(message)
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    /**
     * Update log display
     */
    private fun updateLog(log: String?) {
        if (!log.isNullOrEmpty()) {
            tvLog.text = log
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()
    }

    /**
     * Save instance state before Activity recreation
     * (e.g., when Day/Night mode changes)
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PREPAY_ID, etPrepayId.text.toString())
        outState.putString(STATE_LOG_TEXT, tvLog.text.toString())
    }

    /**
     * Handle configuration changes (Day/Night mode)
     * Finish current Activity and restart with new theme
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Save current state to Intent
        val restartIntent = Intent(this, MainActivity::class.java).apply {
            putExtra(STATE_PREPAY_ID, etPrepayId.text.toString())
            putExtra(STATE_LOG_TEXT, tvLog.text.toString())
            // Also preserve the original prepayId from intent
            intent.getStringExtra(EXTRA_PREPAY_ID)?.let {
                putExtra(EXTRA_PREPAY_ID, it)
            }
        }

        // Finish current and start new
        finish()
        startActivity(restartIntent)
    }
}
