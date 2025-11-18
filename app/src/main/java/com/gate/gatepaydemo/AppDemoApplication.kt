package com.gate.gatepaydemo

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.gate.gatepaydemo.config.DemoConfig
import com.gateio.sdk.gatepay.base.GatePaySDK
import com.gateio.sdk.gatepay.base.PaySdkColor
import com.gateio.sdk.gatepay.base.PayThemeType
import com.gateio.sdk.gatepay.bean.OrderDetailBean
import com.gateio.sdk.gatepay.callback.OrderPageListener

/**
 * Demo Application - Initialize GatePay SDK
 * Must init SDK before any SDK functions
 */
class AppDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initGatePaySDK()
    }

    // Initialize SDK with custom theme
    private fun initGatePaySDK() {
        val customColor = PaySdkColor(
            brandColor = R.color.demo_brand_2_v5,
            brandTagTextColor = R.color.demo_text_always_white_v5,
            buttonTextColor = R.color.demo_text_color_red,
            buttonBackgroundColor = R.color.demo_red_funct_6_v5,
            buttonTextSecondaryColor = R.color.demo_color_17e6a1,
            buttonBackgroundSecondaryColor = R.color.demo_green_funct_6_v5
        )

        GatePaySDK.init(
            isDebug = DemoConfig.ENABLE_DEBUG, // enabled in debug, disabled in release
            applicationContext = this,
            clientId = DemoConfig.SDK_CLIENT_ID,
            appName = DemoConfig.APP_NAME,
            appIcon = DemoConfig.APP_ICON_URL,
            appUrl = "www.gate.com",
            //languageCode = "en",
            //payThemeType = PayThemeType.THEME_MODE_AUTO,
            //payCustomColor = customColor,
            orderListener = createOrderPageListener()
        )
    }

    // Handle order page user interactions
    private fun createOrderPageListener(): OrderPageListener {
        return object : OrderPageListener {
            // User clicks "View Order"
            override fun onRequestViewOrder(context: Context, orderInfo: OrderDetailBean) {
                Toast.makeText(context, "View Order", Toast.LENGTH_SHORT).show()
            }

            // User clicks "Complete" button
            override fun onRequestOpenReturnUrl(context: Context, returnUrl: String) {
                Toast.makeText(context, "Complete", Toast.LENGTH_SHORT).show()
            }

            // User clicks "Contact Support"
            override fun onRequestOpenContactSupport(context: Context, url: String) {
                Toast.makeText(context, "Contact Support", Toast.LENGTH_SHORT).show()
            }
        }
    }
}