# Gate Pay Android SDK 接入指南

[English](README.md) | 简体中文

## 概述

Gate Pay Android SDK 是一个为 Android 应用提供加密货币支付功能的 SDK。它支持多种支付方式，包括 Gate 支付、钱包支付、扫码支付，并提供了完整的支付流程和 UI 自定义能力。

## 环境要求

- Android 5.0 (API Level 21) 及以上
- Kotlin 1.6.0 及以上
- Gradle 7.0 及以上

## 快速开始

**前提条件：** 从 Gate 获取 Client ID、Scheme 和 repos 文件夹（包含 SDK aar）

💡 **Demo 参考：** 完整接入示例请参考 GitHub Demo 工程

### 步骤 1：添加依赖

#### ① 配置依赖仓库 — `settings.gradle.kts`

**方式一：直接引用 GitHub Gate repos**

```kotlin
allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven { url = uri("https://raw.githubusercontent.com/gate/gatepay-sdk-android/main/repos") } // GitHub Gate repos
    }
}
```

**方式二：引用本地 repos**

1）将 Gate 提供的 `repos` 文件夹复制到项目根目录：

```text
YourProject/
├── repos/              ← Gate 提供的 SDK 仓库文件夹
├── app/
├── settings.gradle.kts
└── build.gradle.kts
```
2）引用本地 repos：
```kotlin
allprojects {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven { url = uri("${rootProject.projectDir}/repos") }  // 本地 repos
    }
}
```
> 💡 本地 repos 如需升级 SDK 版本，可将 `repos` 中的旧版本移除，并将依赖版本号修改为最新版本

#### ② 新增依赖 — `build.gradle.kts`

```kotlin
dependencies {
    implementation("com.gateio.sdk:gatepay-sdk:VERSION")  // 替换为 repos 中的实际版本号
}

android {
    defaultConfig {
        manifestPlaceholders["GATEPAY_SCHEME"] = "YOUR_SCHEME_HERE"  // Gate 提供的 Scheme
    }
}
```

💡 找不到提供的 Scheme？可以调用 `GatePaySDK.getSchemeByClientId()` 传入 `clientId` 获取。

### 步骤 2：初始化 SDK

在 `Application.onCreate` 中初始化（必须在 Application 中，不要在 Activity 中）：

```kotlin
GatePaySDK.init(
    isDebug = true,     // 是否开启 Debug，发布线上要改成 false！！
    applicationContext = this,                // Application context
    clientId = "YOUR_CLIENT_ID",              // Gate 提供的 Client ID
    appName = "YOUR_APP_NAME",                // 应用名称
    appIcon = "https://your.cdn/icon.png",    // 应用图标 URL
    orderListener = OrderPageListener         // 订单页事件监听器
)
```

💡 需要自定义语言、主题或颜色？查看 UI 自定义部分

### 步骤 3：发起支付（收银台）

```kotlin
GatePaySDK.openGatePay(
    activity = this,          // 当前 Activity
    signature = signature,    // 支付签名（服务端生成）
    timestamp = timestamp,    // 时间戳（服务端生成）
    nonce = nonce,            // 随机串 16~32 位（服务端生成）
    prepayId = prepayId,      // 预支付订单号（服务端返回）
    packageExt = "GatePay"    // 可选：扩展字段（默认："GatePay"）
)
```

⚠️ **安全规范：** 所有签名参数 (signature/timestamp/nonce/prepayId) 必须由服务端生成并下发，客户端仅透传，不参与签名计算。对账以服务端异步通知为准。

📖 **服务端对接：** 服务端如何生成签名及对接 API，请参考 [Gate Pay 服务端文档](https://www.gate.com/docs/gatepay/common/en/)

## UI 自定义

SDK 支持自定义语言、主题和颜色。

### 初始化时设置（推荐）

```kotlin
GatePaySDK.init(
    isDebug = true,     // 是否开启 Debug，发布线上要改成 false！！
    applicationContext = this,
    clientId = "YOUR_CLIENT_ID",
    appName = "YOUR_APP_NAME",
    appIcon = "https://your.cdn/icon.png",
    languageCode = "en",                         // 可选：语言代码（默认跟随系统）
    payThemeType = PayThemeType.THEME_MODE_DAY,  // 可选：主题（默认 AUTO）
    payCustomColor = PaySdkColor(                // 可选：自定义颜色（默认 Gate 深色）
        // 更多颜色配置...
    ),
    orderListener = OrderPageListener
)
```

### 运行时动态设置

初始化后可随时调整：

```kotlin
// 单独设置语言
GatePaySDK.applyUiSettings(UiSettings(languageCode = "zh"))

// 单独设置主题
GatePaySDK.applyUiSettings(UiSettings(themeType = PayThemeType.THEME_MODE_NIGHT))

// 单独设置颜色
GatePaySDK.applyUiSettings(UiSettings(customColor = PaySdkColor(
    // 更多颜色配置...
)))

// 统一设置
GatePaySDK.applyUiSettings(
    UiSettings(
        languageCode = "en",
        themeType = PayThemeType.THEME_MODE_DAY,
        customColor = PaySdkColor(
            // 更多颜色配置...
        )
    )
)
```

💡 详细配置说明请查看 API 参考—UI 配置说明部分

## API 参考

### OrderPageListener 说明

`OrderPageListener` 用于监听订单页面的用户交互事件。

**实现示例：**

```kotlin
val orderListener = object : OrderPageListener {
    override fun onRequestViewOrder(context: Context, orderInfo: OrderDetailBean) {
        //"查看订单"点击时触发，接入方自行跳转到订单详情页
    }

    override fun onRequestOpenReturnUrl(context: Context, returnUrl: String) {
        // "完成"点击时触发，接入方在应用内打开 returnUrl 或跳转到指定页面
    }

    override fun onRequestOpenContactSupport(context: Context, url: String) {
        // "联系客服"点击时触发，接入方自行跳转到客服页
    }
}
```

**回调方法说明：**

| 方法 | 触发时机 | 参数 | 默认行为（未实现行为） | 建议实现 |
|------|---------|------|---------------------|---------|
| `onRequestViewOrder` | 用户点击"查看订单" | `context`：上下文，`orderInfo`：订单详情对象 | 无操作 | 跳转到商户 App 订单详情页 |
| `onRequestOpenReturnUrl` | 用户点击"完成"按钮 | `context`：上下文，`returnUrl`：返回链接 | 无操作 | 在 App 内跳转或打开指定页面 |
| `onRequestOpenContactSupport` | 用户点击"联系客服" | `context`：上下文，`url`：客服链接 | 无操作 | 跳转到商户客服页面或打开链接 |

⚠️ **注意：**
- 需实现所有回调方法，以提供更好的用户体验。

### UI 配置说明

**支持的语言代码：**

```kotlin
GatePaySDK.applyUiSettings(UiSettings(languageCode = "zh"))
```

| 代码 | 语言 | 代码 | 语言 |
|------|------|------|------|
| `zh` | 简体中文 | `ja` | 日语 |
| `tw` | 繁体中文 | `ko` | 韩语 |
| `en` | 英语 | `vi` | 越南语 |
| `in` | 印尼语 | `th` | 泰语 |
| `es` | 西班牙语 | `fr` | 法语 |
| `de` | 德语 | `it` | 意大利语 |
| `pt` | 葡萄牙语 | `br` | 巴西葡萄牙语 |
| `ru` | 俄语 | `ar` | 阿拉伯语 |
| `tr` | 土耳其语 | `uk` | 乌克兰语 |

**主题类型：**

```kotlin
GatePaySDK.applyUiSettings(UiSettings(themeType = PayThemeType.THEME_MODE_NIGHT))
```

- `THEME_MODE_AUTO`—跟随系统（默认）
- `THEME_MODE_DAY`—白天模式
- `THEME_MODE_NIGHT`—夜间模式

**PaySdkColor 颜色配置：**

```kotlin
GatePaySDK.applyUiSettings(
    UiSettings(
        customColor = PaySdkColor(
            brandColor = R.color.your_brand_color,
            brandTagTextColor = R.color.your_brand_text_color,
            buttonTextColor = R.color.your_button_text_color,
            buttonTextSecondaryColor = R.color.your_button_text_sec,
            buttonBackgroundColor = R.color.your_button_bg,
            buttonBackgroundSecondaryColor = R.color.your_button_bg_sec
        )
    )
)
```

| 参数 | 类型 | 说明 |
|------|------|------|
| `brandColor` | `@ColorRes Int` | 品牌色（主色调） |
| `brandTagTextColor` | `@ColorRes Int` | 品牌标签文字色 |
| `buttonTextColor` | `@ColorRes Int` | 主按钮文字颜色 |
| `buttonTextSecondaryColor` | `@ColorRes Int` | 次要按钮文字颜色 |
| `buttonBackgroundColor` | `@ColorRes Int` | 主按钮背景颜色 |
| `buttonBackgroundSecondaryColor` | `@ColorRes Int` | 次要按钮背景颜色 |

**提示：** 颜色资源使用 `R.color.your_color_name`。如需支持深色模式，请配置 `values/colors.xml` 和 `values-night/colors.xml` 两套资源。

### Groovy 配置

如果使用 `settings.gradle`（Groovy 语法）：

```groovy
allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
        maven { url "${rootProject.projectDir}/repos" }
    }
}
```

## 错误码与排查

### 状态码说明

| 状态码 | 场景 | 处理建议 |
|--------|------|---------|
| 10010 | ✅ 支付完成 | 等待服务端通知确认 |
| 10006 | ❌ 用户取消 | 提示"支付已取消" |
| 10005 | ❌ 支付失败 | 见下方排查步骤 |
| 10024 | ⚠️ 参数错误 | 检查 SDK 初始化参数 |

### 常见错误排查

**❌ 支付失败 (10005)**

可能原因及排查：

1. **签名问题**—确认 signature/timestamp/nonce/prepayId 一致且未过期，检查服务端签名算法
2. **配置问题**—确认 clientId 正确，baseUrl 包含 https:// 前缀，环境配置匹配
3. **网络问题**—确认网络可达，SSL 证书有效，无代理拦截
4. **高级调试**—使用抓包工具查看请求参数，必要时联系技术支持

**⚠️ 参数错误 (10024)**

解决方案：

1. 确保在 `Application.onCreate` 中调用 `GatePaySDK.init`
2. 确保 `clientId` 已替换为实际值（非 `YOUR_CLIENT_ID`）
3. 检查 `AndroidManifest.xml` 是否声明自定义 Application
4. 查看 Logcat 确认初始化日志

## 集成检查清单

完成集成后，请逐项检查：

- [✅] repos 文件夹已放置到项目根目录
- [✅] `settings.gradle.kts` 已添加 Maven 仓库配置
- [✅] `build.gradle.kts` 已添加 SDK 依赖
- [✅] `GATEPAY_SCHEME` 已配置为 Gate 提供的实际值
- [✅] `Application.onCreate` 中已调用 `GatePaySDK.init`
- [✅] `clientId` 已替换为实际值（非占位符）
- [✅] 验证整个支付流程
- [✅] 已配置服务端异步通知接口

## 常见问题

**Q1：如何查看 Debug 日志？**

修改 `GatePaySDK.init` 的 `isDebug` 参数：`isDebug = true,` 开启调试模式，切记发布线上要改成 false

**Q2：是否需要混淆配置？**

SDK 已内置混淆规则，无需额外配置。如有问题可添加：`-keep class com.gateio.sdk.gatepay.** { *; }`

**Q3：如何验证集成成功？**

初始化后查看 Logcat 日志，调用支付观察是否跳转收银台，建议先在 Debug 环境走通流程。

**Q4：支付结果如何确认？**

收银台页面自动处理，商户无需额外代码。商户要以服务端异步通知为准。

**Q5：支持哪些加密货币？**

具体币种由商户配置决定，详见 Gate Pay 商户后台。

**Q6：报错 "SDK not initialized" 怎么办？**

确认在 `Application.onCreate` 中调用了 `GatePaySDK.init`，且初始化在支付调用之前执行。

## 更多版本选择

如有特殊需求，可选择其他版本：

| 版本                 | 依赖 | 初始化 / 调用类名      | 适用场景                          |
|--------------------|------|-----------------|-------------------------------|
| **Full（当前版本<推荐>）** | `gatepay-sdk` | `GatePaySDK`    | 完整收银台，支持扫码转账、钱包支付（包含：WalletConnect、Phantom、Bitget Wallet） |
| **Simple**         | `gatepay-sdk-simple` | `GatePaySimple` | 轻量收银台，包体积更小，仅支持扫码转账，不支持钱包支付     |
| **Lite**           | `gatepay-sdk-lite` | `GatePayLite`   | 仅支持跳转 Gate App 支付           |

**切换方式：** 替换依赖 + 类名

**Simple**
```kotlin
implementation("com.gateio.sdk:gatepay-sdk-simple:VERSION")
GatePaySimple.init()  // 参数同 Full
GatePaySimple.openGatePay()  // 参数同 Full
```

**Lite**
```kotlin
implementation("com.gateio.sdk:gatepay-sdk-lite:VERSION")
GatePayLite.init(isDebug, applicationContext, clientId)  // 可只传 3 个参数
GatePayLite.openGatePay()  // 参数同 Full
```

> 💡 **Lite 需自行处理回调：** Gate App 支付完成后通过 Deep Link 回调，需在 `AndroidManifest.xml` 配置接收 Activity：
> ```xml
> <activity android:name=".YourCallbackActivity" android:exported="true">
>     <intent-filter>
>         <action android:name="android.intent.action.VIEW" />
>         <category android:name="android.intent.category.DEFAULT" />
>         <data android:scheme="gatepay******" android:host="payment" />
>     </intent-filter>
> </activity>
> ```
> ⚠️ `gatepay******` 为 Gate 根据 clientId 生成的 Scheme，请替换为实际值
>
> 解析 `intent.data` 获取结果：`isSuccess`（1=成功/0=失败/2=取消）、`prepayId`（预支付ID）
>
> 示例：`gatepaya1b2c3://payment?isSuccess=1&source=gatePay&prepayId=123435567`

## 技术支持

如有疑问或需要帮助，请联系：

- 📖 GitHub：https://github.com/gate
- 💬 技术支持：联系商务获取

## 版本更新日志

### v3.0.1（当前版本）

- ✨ 收银台 UI 全新改版
- 🔄 新增多币种、多网络闪兑支付
- 💰 新增 Phantom 钱包支持
- 🌐 新增 Bitget Solana (SOL)、Tron (TRX) 链支持

### v2.0.0

- 📱 提供完整的收银台 UI
- ✨ Gate 调起支付新增扫码支付功能
- ✨ 新增 Web3 支付功能（支持 WalletConnect 协议）
- 🎨 新增主题切换支持（白天/夜间/跟随系统）
- 🌍 新增多语言切换功能（支持 18+ 种语言）
- 🎨 新增 UI 自定义颜色配置
- 🔧 优化支付流程和用户体验

### v1.0.0

- 🚀 初始版本发布
- 💰 支持 Gate 调起支付功能
- 🔐 支持服务端签名验证
