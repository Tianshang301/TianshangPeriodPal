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

**A fully offline, privacy-focused menstrual cycle tracking and management app**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 Feature Preview

> 💡 **Tip**: Download the latest APK from the [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) page.

### Core Features
| Feature | Description |
|--------|-------------|
| 📅 **Cycle Recording** | Record period start/end dates, flow level, pain level, 10 symptoms |
| 🔮 **Smart Prediction** | Predict next 6 cycles with ovulation and fertile window using statistical algorithms |
| 📊 **Data Analysis** | Cycle regularity analysis, pain trends, symptom frequency statistics |
| 🎨 **Theme Customization** | 5 pink themes + custom background image (with transparency control) |
| 🌗️ **Dark Mode** | Follow system or manual toggle |
| 🔒 **App Lock** | Fingerprint/facial recognition + PIN, Argon2 hash encryption |
| 🌐 **Multi-Language** | Supports 7 languages (zh/en/ja/ko/fr/es/ar) |
| 💾 **Data Management** | Database backup (ZIP), CSV export, recycle bin (auto-cleanup after 30 days) |
| ⚖️ **BMI Tracking** | Calculate BMI and track history (Chinese standards) |
| ⏰ **Reminder System** | 3 reminder types (period/ovulation/PMS), customizable days in advance and time |

---

## 🏗️ Technical Architecture

### Architecture Pattern
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

### Tech Stack
| Category | Library/Framework | Version |
|----------|------------------|---------|
| **Language** | Kotlin | 1.9.20 |
| **UI Framework** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **Navigation** | Compose Navigation | 2.7.6 |
| **Architecture** | MVVM + Repository | - |
| **Database** | Room + SQLite | 2.6.1 |
| **Local Storage** | DataStore Preferences | 1.0.0 |
| **Background** | WorkManager | 2.9.0 |
| **Biometrics** | AndroidX Biometric | 1.1.0 |
| **Image Loading** | Coil Compose | 2.5.0 |
| **Security** | Argon2 (Bouncy Castle) | 1.78 |
| **Data Export** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **Build Tool** | Gradle | 8.2 |
| **Compile** | JDK | 17 |

---

## 📲 Screen Guide

### Bottom Navigation (5 tabs)

| Screen | Function |
|-------|----------|
| 📅 **Calendar** | Displays periods (red), predictions (light red), ovulation (blue), fertile window (light blue); tap dates to record |
| 📝 **Record** | Record flow, pain, 10 symptoms, sexual activity, ovulation test, cervical mucus, body temperature |
| 📊 **Analysis** | Cycle statistics, pain trends, symptom frequency, next 3 cycles prediction |
| ⏰ **Reminder** | Configure period/ovulation/PMS reminders (toggle, days in advance, time) |
| 👤 **Profile** | Quick access: theme, language, BMI, app lock, recycle bin, backup |

### Secondary Screens

- **Theme Customization**: 5 pink themes + custom background + transparency slider
- **Language Selection**: 7 languages, instant switching (AppCompatDelegate)
- **BMI Calculator**: Input height/weight, view history
- **App Lock**: First-time PIN setup, biometric (fingerprint/facial) support
- **Recycle Bin**: Restore records deleted within 30 days
- **Backup & Restore**: Export ZIP database / Import backup / CSV export
- **Settings**: App lock toggle, screenshot prevention toggle

---

## 🔒 Privacy & Security

### Privacy Features
- ✅ **Fully Offline** — Zero network requests, no permissions required
- ✅ **Local Storage** — All data stored locally on device only
- ✅ **App Lock** — Fingerprint/facial recognition + PIN code
- ✅ **Screenshot Prevention** — Enable FLAG_SECURE to block screenshots

### Note on Encryption
> ⚠️ **Important**: SQLCipher database encryption was initially considered but ultimately abandoned. Data is currently stored in **plaintext** in the local SQLite database. The app lock only protects access to the app, not the database file itself.

### Security Recommendations
1. Enable the app lock feature
2. Regularly export data using the backup function
3. Set a screen lock on your device

---

## 🚀 Quick Start

### Prerequisites
- JDK 17 (Eclipse Adoptium JDK 17 recommended)
- Gradle 8.2 (Gradle Wrapper included)
- Android SDK 34

### Build Commands

```bash
# Clone the project
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# Set JDK 17
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# Build Debug version
.\gradlew.bat assembleDebug

# Build Release version (signature configuration required)
.\gradlew.bat assembleRelease
```

### Release Signing Configuration

Already configured in `app/build.gradle`:
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

## 🛣️ Development Roadmap

- [x] 1. Project initialization & architecture setup
- [x] 2. Room database & local storage
- [x] 3. Terms of service & app lock
- [x] 4. Cycle recording features
- [x] 5. Prediction engine (statistical algorithms)
- [x] 6. Data analysis & statistics
- [x] 7. Reminder system (WorkManager)
- [x] 8. Multi-language support (7 languages)
- [x] 9. Theme customization & dark mode
- [x] 10. Recycle bin, backup & CSV export
- [x] 11. BMI calculation & tracking
- [x] 12. Testing & bug fixes
- [ ] 13. Symptom chart visualization (MPAndroidChart)
- [ ] 14. Home screen widget
- [ ] 15. Integration research with similar open source projects

---

## 📦 Project Structure

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12 screens
│   │   │   │   ├── theme/        # Material3 theme
│   │   │   │   └── navigation/   # NavHost
│   │   │   ├── viewmodel/     # 9 ViewModels
│   │   │   ├── data/
│   │   │   │   ├── model/      # Data models
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Repository layer
│   │   │   ├── utils/         # Utilities
│   │   │   └── service/       # Background services
│   │   └── res/
│   │       ├── values/        # English strings
│   │       ├── values-zh/     # Simplified Chinese
│   │       ├── values-ja/     # Japanese
│   │       ├── values-ko/     # Korean
│   │       ├── values-fr/     # French
│   │       ├── values-es/     # Spanish
│   │       └── values-ar/     # Arabic
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 Contributing

Issues and Pull Requests are welcome!

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
