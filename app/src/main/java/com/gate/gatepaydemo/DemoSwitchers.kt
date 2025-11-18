package com.gate.gatepaydemo

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.gateio.sdk.gatepay.base.PaySdkColor
import com.gateio.sdk.gatepay.base.GatePaySDK
import com.gateio.sdk.gatepay.base.PayThemeType
import com.gateio.sdk.gatepay.base.UiSettings

/**
 * Demo UI Switchers - Runtime SDK UI Configuration
 * Demonstrates GatePaySDK.applyUiSettings() usage
 */
object DemoSwitchers {

    // Theme mode constants (matching SDK internal values)
    private const val THEME_MODE_AUTO = 0
    private const val THEME_MODE_DAY = 1
    private const val THEME_MODE_NIGHT = 2

    // Language constants (matching SDK's LocalUtils)
    private const val LANG_EN = "en"       // English
    private const val LANG_ZH = "zh"       // 简体中文
    private const val LANG_TW = "tw"       // 繁体中文（台湾）
    private const val LANG_HK = "hk"       // 繁体中文（香港）
    private const val LANG_JA = "ja"       // 日本語
    private const val LANG_KO = "ko"       // 한국어
    private const val LANG_ES = "es"       // Español
    private const val LANG_FR = "fr"       // Français
    private const val LANG_DE = "de"       // Deutsch
    private const val LANG_RU = "ru"       // Русский
    private const val LANG_AR = "ar"       // العربية
    private const val LANG_TR = "tr"       // Türkçe
    private const val LANG_PT = "pt"       // Português
    private const val LANG_VI = "vi"       // Tiếng Việt
    private const val LANG_TH = "th"       // ไทย
    private const val LANG_IN = "in"       // Bahasa Indonesia (注意：SDK中使用"in"而不是"id")

    // Supported languages list (display name to language code)
    private val SUPPORTED_LANGUAGES = listOf(
        "English" to LANG_EN,
        "简体中文" to LANG_ZH,
        "繁體中文（台灣）" to LANG_TW,
        "繁體中文（香港）" to LANG_HK,
        "日本語" to LANG_JA,
        "한국어" to LANG_KO,
        "Español" to LANG_ES,
        "Français" to LANG_FR,
        "Deutsch" to LANG_DE,
        "Русский" to LANG_RU,
        "العربية" to LANG_AR,
        "Türkçe" to LANG_TR,
        "Português" to LANG_PT,
        "Tiếng Việt" to LANG_VI,
        "ไทย" to LANG_TH,
        "Bahasa Indonesia" to LANG_IN
    )

    // Show language picker dialog
    fun showLanguagePicker(activity: Activity) {
        val names = SUPPORTED_LANGUAGES.map { it.first }.toTypedArray()
        val codes = SUPPORTED_LANGUAGES.map { it.second }

        // Get current language from SDK, default to English
        val currentCode = GatePaySDK.getCurrentLanguage().ifEmpty { LANG_EN }
        val prechecked = codes.indexOfFirst { it.equals(currentCode, ignoreCase = true) }.let { if (it >= 0) it else 0 }

        AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_switch_language)
            .setSingleChoiceItems(names, prechecked) { dialog, which ->
                val lang = codes.getOrNull(which) ?: LANG_EN
                GatePaySDK.applyUiSettings(UiSettings(languageCode = lang))
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    // Predefined themes
    private val THEME_LIGHT_BLUE = PaySdkColor(
        brandColor = R.color.demo_brand_2_v5,
        brandTagTextColor = R.color.demo_text_always_white_v5,
        buttonTextColor = R.color.demo_white,
        buttonBackgroundColor = R.color.demo_brand_4_v5,
        buttonTextSecondaryColor = R.color.demo_orange_2_v5,
        buttonBackgroundSecondaryColor = R.color.demo_orange_5_v5
    )

    private val THEME_RED = PaySdkColor(
        brandColor = R.color.demo_text_color_red,
        brandTagTextColor = R.color.demo_text_always_white_v5,
        buttonTextColor = R.color.demo_white,
        buttonBackgroundColor = R.color.demo_red_funct_6_v5,
        buttonTextSecondaryColor = R.color.demo_white,
        buttonBackgroundSecondaryColor = R.color.demo_green_funct_6_v5
    )

    // Show theme color picker dialog with preset themes
    fun showThemeColorPicker(activity: Activity) {
        val colors = arrayOf(
            activity.getString(R.string.theme_default),     // 默认主题
            activity.getString(R.string.theme_light_blue),  // 浅蓝主题
            activity.getString(R.string.theme_red)          // 红色主题
        )

        // Note: We can't detect current theme, so no pre-selection
        AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_theme_color)
            .setSingleChoiceItems(colors, -1) { dialog, which ->
                when (which) {
                    0 -> {
                        // 默认主题 - 清除自定义主题
                        GatePaySDK.clearCustomTheme()
                    }
                    1 -> {
                        // 浅蓝主题
                        GatePaySDK.applyUiSettings(UiSettings(customColor = THEME_LIGHT_BLUE))
                    }
                    2 -> {
                        // 红色主题
                        GatePaySDK.applyUiSettings(UiSettings(customColor = THEME_RED))
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }

    // Show day/night mode picker: Auto / Day / Night
    fun showDayNightPicker(activity: Activity) {
        val modes = arrayOf(
            activity.getString(R.string.theme_auto),
            activity.getString(R.string.theme_day),
            activity.getString(R.string.theme_night)
        )
        val prechecked = when (GatePaySDK.getCurrentThemeMode()) {
            THEME_MODE_DAY -> 1
            THEME_MODE_NIGHT -> 2
            else -> 0
        }

        AlertDialog.Builder(activity)
            .setTitle(R.string.dialog_day_night)
            .setSingleChoiceItems(modes, prechecked) { dialog, which ->
                // Check if selection is different from current
                val currentMode = GatePaySDK.getCurrentThemeMode()
                val selectedMode = which

                // If same as current, just dismiss
                if ((currentMode == THEME_MODE_AUTO && selectedMode == 0) ||
                    (currentMode == THEME_MODE_DAY && selectedMode == 1) ||
                    (currentMode == THEME_MODE_NIGHT && selectedMode == 2)) {
                    dialog.dismiss()
                    return@setSingleChoiceItems
                }

                val type = when (which) {
                    1 -> PayThemeType.THEME_MODE_DAY
                    2 -> PayThemeType.THEME_MODE_NIGHT
                    else -> PayThemeType.THEME_MODE_AUTO
                }

                dialog.dismiss()

                // Important: GatePaySDK.applyUiSettings() will trigger Activity recreation automatically
                // DO NOT call activity.recreate() manually, or you'll get duplicate Activities!
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    GatePaySDK.applyUiSettings(UiSettings(themeType = type))
                }, 150)
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .show()
    }
}


