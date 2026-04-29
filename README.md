<p align="center">
  <strong>🌐 Languages</strong><br>
  <a href="README.md">English</a> ·
  <a href="readme/README.zh_CN.md">简体中文</a> ·
  <a href="readme/README.ja.md">日本語</a> ·
  <a href="readme/README.ko.md">한국어</a> ·
  <a href="readme/README.fr.md">Français</a> ·
  <a href="readme/README.es.md">Español</a> ·
  <a href="readme/README.ar.md">العربية</a>
</p>

---

<div align="center">

# 🌸 TianshangPeriodPal (天殇·月记)

**一款完全离线、注重隐私的月经周期跟踪与管理工具**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 功能预览

> 💡 **提示**：您可以在 [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) 页面下载最新 APK 安装体验。

### 核心功能
| 功能 | 描述 |
|------|------|
| 📅 **周期记录** | 记录经期起止日期、流量、疼痛程度、10种症状 |
| 🔮 **智能预测** | 基于统计学算法预测未来6个周期，含排卵期和易孕期 |
| 📊 **数据分析** | 周期规律性分析、疼痛趋势、症状频率统计 |
| 🎨 **主题定制** | 5种粉色系主题 + 自定义背景图片（支持透明度调节）|
| 🌗️ **深色模式** | 支持跟随系统或手动切换 |
| 🔒 **应用锁** | 指纹/面部识别 + PIN 码，Argon2 哈希加密 |
| 🌐 **多语言** | 支持 7 种语言（中/英/日/韩/法/西/阿）|
| 💾 **数据管理** | 数据库备份(ZIP)、CSV 导出、回收站(30天自动清理) |
| ⚖️ **BMI 追踪** | 计算 BMI 并追踪历史记录（中国标准）|
| ⏰ **提醒系统** | 经期/排卵/PMS 三种提醒，可自定义提前天数和时间 |

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
│    Room DB (SQLite)  +  DataStore        │
└─────────────────────────────────────────────┘
```

### 技术栈
| 类别 | 库/框架 | 版本 |
|------|----------|------|
| **语言** | Kotlin | 1.9.20 |
| **UI 框架** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **导航** | Compose Navigation | 2.7.6 |
| **架构** | MVVM + Repository | - |
| **数据库** | Room + SQLite | 2.6.1 |
| **本地存储** | DataStore Preferences | 1.0.0 |
| **后台任务** | WorkManager | 2.9.0 |
| **生物识别** | AndroidX Biometric | 1.1.0 |
| **图片加载** | Coil Compose | 2.5.0 |
| **数据安全** | Argon2 (Bouncy Castle) | 1.78 |
| **数据导出** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **构建工具** | Gradle | 8.2 |
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

### 隐私特性
- ✅ **完全离线** — 零网络请求，无需任何权限
- ✅ **本地存储** — 所有数据仅存储在设备本地
- ✅ **应用锁** — 支持指纹/面部识别 + PIN 码
- ✅ **截图防护** — 可开启禁止截图模式（FLAG_SECURE）

### 关于加密说明
> ⚠️ **注意**：项目初期曾考虑使用 SQLCipher 进行数据库加密，但最终放弃。当前数据以**明文形式**存储在本地 SQLite 数据库中。应用锁仅保护应用访问，不加密数据库文件本身。

### 数据安全建议
1. 开启应用锁功能
2. 定期使用备份功能导出数据
3. 设备建议设置屏幕锁

---

## 🚀 快速开始

### 前置要求
- JDK 17（推荐 Eclipse Adoptium JDK 17）
- Gradle 8.2（项目自带 Gradle Wrapper）
- Android SDK 34

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
        storePassword 'periodpal123'
        keyAlias 'periodpal'
        keyPassword 'periodpal123'
    }
}
```

---

## 🛣️ 开发路线图

- [x] 1. 项目初始化与架构搭建
- [x] 2. Room 数据库与本地存储
- [x] 3. 用户协议与应用锁
- [x] 4. 周期记录功能
- [x] 5. 预测引擎（统计学算法）
- [x] 6. 数据分析与统计
- [x] 7. 提醒系统（WorkManager）
- [x] 8. 多语言支持（7种语言）
- [x] 9. 主题定制与深色模式
- [x] 10. 回收站、备份与 CSV 导出
- [x] 11. BMI 计算与追踪
- [x] 12. 测试与 Bug 修复
- [ ] 13. 经期症状图表可视化（MPAndroidChart）
- [ ] 14. 小组件（Home Screen Widget）
- [ ] 15. 同名开源项目整合研究

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

本项目基于 [MIT License](LICENSE) 开源发布。

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
