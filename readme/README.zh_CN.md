<p align="center">
  <strong>🌐 语言</strong><br>
  <a href="../README.md">English</a> ·
  <a href="README.zh_CN.md">简体中文</a> ·
  <a href="README.ja.md">日本語</a> ·
  <a href="README.ko.md">한국어</a> ·
  <a href="README.fr.md">Français</a> ·
  <a href="README.es.md">Español</a> ·
  <a href="README.ar.md">العربية</a>
</p>

---

<div align="center">

# 🌸 TianshangPeriodPal（天殇·月记）

**一款完全离线、注重隐私的月经周期跟踪与管理工具**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Version](https://img.shields.io/badge/Version-2.1.0-blue.svg)](https://github.com/Tianshang301/TianshangPeriodPal/releases)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.10.00-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 功能预览

> 💡 **提示**：您可以在 [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) 页面下载最新 APK 安装体验。

### 核心功能
| 功能 | 描述 |
|------|------|
| 📅 **周期记录** | 记录经期起止日期、流量、疼痛程度、10+ 种症状 |
| 🔮 **智能预测** | 基于自适应统计学算法预测未来6个周期，含排卵期和易孕期 |
| 📊 **数据分析** | 周期规律性分析、疼痛趋势、症状频率统计 |
| 🎨 **主题定制** | HSL 色彩引擎 + 自定义背景图片（支持透明度调节）|
| 🌗️ **深色模式** | 支持跟随系统或手动切换 |
| 🔒 **应用锁** | 指纹/面部识别 + PIN 码，Argon2id 哈希加密 |
| 🌐 **多语言** | 支持 7 种语言（中/英/日/韩/法/西/阿 + RTL 适配）|
| 💾 **数据管理** | 加密数据库备份(ZIP)、CSV 导出、回收站(30天自动清理) |
| ⚖️ **BMI 追踪** | 计算 BMI 并追踪历史记录（中国标准）|
| ⏰ **提醒系统** | 经期/排卵/PMS 三种提醒 + 自定义提醒，可配置提前天数和时间 |
| 🔐 **数据库加密** | SQLCipher AES-256 加密，Android Keystore 密钥管理 |

---

## 🏗️ 技术架构

### 架构模式
```
┌─────────────────────────────────────┐
│                 UI Layer (Compose)            │
│  Calendar  Record  Analysis  Reminder  Profile │
│         ↓         ↓         ↓         ↓        │
│         └──────────┬──────────┘         │
│                    ↓                      │
│           ViewModel (StateFlow)          │
└────────────────────┬─────────────────────┘
                     ↓
┌────────────────────┴─────────────────────┐
│            Repository Layer                  │
│   PeriodRepo  SymptomRepo  SettingsRepo  │
└────────────────────┬─────────────────────┘
                     ↓
┌────────────────────┴─────────────────────┐
│              Data Layer                      │
│    Room DB (SQLCipher)  +  DataStore        │
└─────────────────────────────────────────────┘
```

### 技术栈
| 类别 | 库/框架 | 版本 |
|------|----------|------|
| **语言** | Kotlin | 1.9.24 |
| **UI 框架** | Jetpack Compose (Material 3) | BOM 2025.10.00 |
| **导航** | Compose Navigation | 2.7.6 |
| **架构** | MVVM + Repository | - |
| **数据库** | Room + SQLCipher | 2.6.1 / 4.5.4 |
| **本地存储** | DataStore Preferences | 1.0.0 |
| **后台任务** | WorkManager | 2.9.0 |
| **生物识别** | AndroidX Biometric | 1.1.0 |
| **图片加载** | Coil Compose | 2.5.0 |
| **安全** | Argon2 (Bouncy Castle) + Android Keystore | 1.78 |
| **数据导出** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **构建工具** | Gradle | 8.9 |
| **编译** | JDK | 17 |

---

## 📲 屏幕说明

### 底部导航（5 个标签页）

| 屏幕 | 功能 |
|------|------|
| 📅 **日历** | 显示经期（红色）、预测（浅红）、排卵（蓝色）、易孕（浅蓝）；点击日期可记录 |
| 📝 **记录** | 记录流量、疼痛、10种症状、性生活、排卵测试、宫颈粘液、体温 |
| 📊 **分析** | 周期统计、疼痛趋势图、症状频率、未来3周期预测 |
| ⏰ **提醒** | 配置经期/排卵/PMS 提醒的开关、提前天数、提醒时间 |
| 👤 **我的** | 快捷入口：主题、语言、BMI、应用锁、回收站、备份 |

### 二级界面

- **主题定制**：5种粉色主题 + 自定义背景图片 + 透明度调节
- **语言选择**：7种语言即时切换（AppCompatDelegate）
- **BMI 计算**：输入身高体重，查看历史记录
- **应用锁**：首次设置 PIN，后续支持指纹/面部识别
- **回收站**：恢复30天内删除的记录
- **备份恢复**：导出 ZIP 数据库 / 导入备份 / CSV 导出
- **设置**：应用锁开关、截图防护开关

---

## 🔒 隐私与安全

### 数据库加密 (v2.0.0)
> ✅ **SQLCipher AES-256 加密**：数据库使用 SQLCipher 的 AES-256-CBC 加密。加密密钥由 Android Keystore（硬件支持）管理。新安装默认启用加密数据库；现有用户可通过 设置 → 数据安全 手动迁移。

### 数据保护机制
- ✅ **完全离线** — 零网络请求，无 INTERNET 权限
- ✅ **数据库加密** — SQLCipher AES-256 + Android Keystore 密钥管理
- ✅ **加密备份** — AES-256-GCM 加密 ZIP + 完整性哈希校验
- ✅ **应用锁** — 指纹/面部识别 + PIN 码（Argon2id 哈希，最短 6 位）
- ✅ **暴力破解防护** — 连续输错 PIN 后自动锁定，逐步延长等待时间
- ✅ **截图防护** — 已启用 FLAG_SECURE 禁止截图（用户可控）
- ✅ **无硬编码密钥** — 构建凭据通过 local.properties 管理（已加入 .gitignore）
- ✅ **后台锁定** — 应用退到后台立即锁定（可配置延时）

### 安全架构
```
用户密码 → Argon2id 哈希 → EncryptedSharedPreferences
                                        ↓
Android Keystore → 主密钥 → 加密 passphrase
                                        ↓
Passphrase → SupportFactory → SQLCipher AES-256 → 加密数据库
```

### 安全最佳实践
1. 设置强设备屏幕锁 — 您的第一道防线
2. 启用应用内应用锁 — 防止他人随意访问
3. 保持系统和安全补丁更新 — 修补已知的 root 漏洞
4. 如需传输数据，请使用加密备份功能

---

## 🚀 快速开始

### 前置要求
- JDK 17（推荐 Eclipse Adoptium JDK 17）
- Gradle 8.9（项目自带 Gradle Wrapper）
- Android SDK 35

### 构建命令

```bash
# 克隆项目
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# 设置 JDK 17
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# 构建 Debug 版本
.\gradlew.bat assembleDebug

# 构建 Release 版本（需配置签名）
.\gradlew.bat assembleRelease
```

### Release 签名配置

在 `app/build.gradle` 中已配置签名：
```gradle
signingConfigs {
    release {
        storeFile file('periodpal-release.jks')
        storePassword '<YOUR_KEYSTORE_PASSWORD>'
        keyAlias '<YOUR_KEY_ALIAS>'
        keyPassword '<YOUR_KEY_PASSWORD>'
    }
}
```

> **注意**: 将 `local.properties.example` 复制为 `local.properties` 并填写签名凭据。切勿将 `local.properties` 提交到版本控制。

---

## 🛣️ 开发路线图

- [x] 1. 项目初始化与架构搭建
- [x] 2. Room 数据库与本地存储
- [x] 3. 用户协议与应用锁
- [x] 4. 周期记录功能
- [x] 5. 预测引擎（自适应统计学算法）
- [x] 6. 数据分析与统计
- [x] 7. 提醒系统（WorkManager）
- [x] 8. 多语言支持（7种语言 + RTL）
- [x] 9. 主题定制（HSL 色彩引擎）与深色模式
- [x] 10. 回收站、加密备份与 CSV 导出
- [x] 11. BMI 计算与追踪
- [x] 12. SQLCipher AES-256 数据库加密
- [x] 13. 预测算法优化（黄体期学习、指数衰减加权）
- [x] 14. 安全审计与漏洞修复
- [ ] 15. 经期症状图表可视化（MPAndroidChart）
- [ ] 16. 小组件（Home Screen Widget）
- [ ] 17. 同名开源项目整合研究

---

## 📦 项目结构

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12个界面
│   │   │   │   ├── theme/        # Material3 主题
│   │   │   │   └── navigation/   # NavHost 导航图
│   │   │   ├── viewmodel/     # 9个 ViewModel
│   │   │   ├── data/
│   │   │   │   ├── model/      # 数据模型
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Repository 层
│   │   │   ├── utils/         # 工具类
│   │   │   └── service/       # 后台服务
│   │   └── res/
│   │       ├── values/        # 英文字符串
│   │       ├── values-zh/     # 简体中文
│   │       ├── values-ja/     # 日文
│   │       ├── values-ko/     # 韩文
│   │       ├── values-fr/     # 法文
│   │       ├── values-es/     # 西班牙文
│   │       └── values-ar/     # 阿拉伯文
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

---

## 📄 许可证

本项目基于 [MIT License](../LICENSE) 开源发布。

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
