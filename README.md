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
[![Version](https://img.shields.io/badge/Version-2.0.0-blue.svg)](https://github.com/Tianshang301/TianshangPeriodPal/releases)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.10.00-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 Feature Preview

> 💡 **Tip**: Download the latest APK from the [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) page.

### Core Features
| Feature | Description |
|--------|-------------|
| 📅 **Cycle Recording** | Record period start/end dates, flow level, pain level, 10+ symptoms |
| 🔮 **Smart Prediction** | Predict next 6 cycles with ovulation and fertile window using adaptive statistical algorithms |
| 📊 **Data Analysis** | Cycle regularity analysis, pain trends, symptom frequency statistics |
| 🎨 **Theme Customization** | HSL color engine with custom color picker + background image (with transparency control) |
| 🌗️ **Dark Mode** | Follow system or manual toggle |
| 🔒 **App Lock** | Fingerprint/facial recognition + PIN, Argon2id hash encryption |
| 🌐 **Multi-Language** | Supports 7 languages (en/zh/ja/ko/fr/es/ar + RTL support) |
| 💾 **Data Management** | Encrypted database backup (ZIP), CSV export, recycle bin (auto-cleanup after 30 days) |
| ⚖️ **BMI Tracking** | Calculate BMI and track history (Chinese standards) |
| ⏰ **Reminder System** | 3 reminder types (period/ovulation/PMS) + custom reminders, configurable days and time |
| 🔐 **Database Encryption** | SQLCipher AES-256 encryption with Android Keystore key management |

---

## 🏗️ Technical Architecture

### Architecture Pattern
```
┌─────────────────────────────────────┐
│            UI Layer (Compose)        │
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

### Tech Stack
| Category | Library/Framework | Version |
|----------|------------------|---------|
| **Language** | Kotlin | 1.9.24 |
| **UI Framework** | Jetpack Compose (Material 3) | BOM 2025.10.00 |
| **Navigation** | Compose Navigation | 2.7.6 |
| **Architecture** | MVVM + Repository | - |
| **Database** | Room + SQLCipher | 2.6.1 / 4.5.4 |
| **Local Storage** | DataStore Preferences | 1.0.0 |
| **Background** | WorkManager | 2.9.0 |
| **Biometrics** | AndroidX Biometric | 1.1.0 |
| **Image Loading** | Coil Compose | 2.5.0 |
| **Security** | Argon2 (Bouncy Castle) + Android Keystore | 1.78 |
| **Data Export** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **Build Tool** | Gradle | 8.9 |
| **Compile** | JDK | 17 |

---

## 📲 Screen Guide

### Bottom Navigation (5 tabs)

| Screen | Function |
|-------|----------|
| 📅 **Calendar** | Displays periods (red), predictions (light red), ovulation (blue), fertile window (light blue); tap dates to record |
| 📝 **Record** | Record flow, pain, 10+ symptoms (customizable), sexual activity, ovulation test, cervical mucus, body temperature |
| 📊 **Analysis** | Cycle statistics, pain trends, symptom frequency, next 3 cycles prediction with explanation |
| ⏰ **Reminder** | Configure period/ovulation/PMS reminders + custom reminders |
| 👤 **Profile** | Settings, theme, language, BMI, recycle bin, backup |

### Secondary Screens

- **Settings**: App lock toggle, background lock delay, screenshot prevention, database encryption status, migration
- **Theme Customization**: HSL color sliders + 5 preset colors + custom background + transparency slider
- **Language Selection**: 7 languages, instant switching (AppCompatDelegate)
- **BMI Calculator**: Input height/weight, view history
- **App Lock**: First-time PIN setup, biometric (fingerprint/facial) support
- **Recycle Bin**: Restore records deleted within 30 days
- **Backup & Restore**: Export encrypted ZIP database / Import backup / CSV export

---

## 🔒 Privacy & Security

### Database Encryption (v2.0.0)
> ✅ **SQLCipher AES-256 Encryption**: The database is encrypted using SQLCipher with AES-256-CBC. The encryption key is managed by Android Keystore (hardware-backed). New installations default to encrypted databases; existing users can migrate manually via Settings → Data Security.

### How Your Data Is Protected
- ✅ **Fully Offline** — Zero network requests, no INTERNET permission
- ✅ **Database Encryption** — SQLCipher AES-256 with Android Keystore key management
- ✅ **Encrypted Backups** — AES-256-GCM encrypted ZIP with integrity hash verification
- ✅ **App Lock** — Fingerprint/facial recognition + PIN (Argon2id hash, minimum 6 digits)
- ✅ **Brute-force Protection** — Exponential lockout after failed PIN attempts
- ✅ **Screenshot Prevention** — FLAG_SECURE enabled (user-controlled)
- ✅ **No Hardcoded Secrets** — Build credentials via local.properties (git-ignored)
- ✅ **Lifecycle Lock** — App locks immediately when backgrounded (configurable delay)

### Security Architecture
```
User Password → Argon2id Hash → EncryptedSharedPreferences
                                        ↓
Android Keystore → Master Key → Encrypts passphrase
                                        ↓
Passphrase → SupportFactory → SQLCipher AES-256 → Encrypted Database
```

### Security Best Practices
1. Set a strong device screen lock — your first line of defense
2. Enable the in-app App Lock — prevents casual unauthorized access
3. Keep your OS and security patches up to date
4. If you need to transfer data, use the encrypted backup feature

---

## 🧠 Prediction Algorithm

### How It Works
The prediction engine uses adaptive statistical algorithms to learn from your personal cycle history:

1. **Cycle Length Calculation** — Computes intervals between consecutive period start dates
2. **IQR Outlier Filtering** — Removes statistical outliers using Interquartile Range method
3. **Exponential Decay Weighting** — Recent cycles have higher influence on predictions
4. **Dynamic Range Adaptation** — Cycle length bounds adapt to your personal data (21-45 days)
5. **Ovulation Correction** — Adjusts predictions based on ovulation tests, cervical mucus, or BBT
6. **Luteal Phase Learning** — Learns your personal luteal phase from historical ovulation data

### Accuracy
| Scenario | Expected Accuracy |
|----------|------------------|
| Regular cycles (CV < 5%) | ±2-3 days |
| Somewhat regular (CV < 10%) | ±3-4 days |
| Irregular cycles (CV > 10%) | ±4-5 days |
| With ovulation data | ±2-3 days (improved) |

### Key Parameters
| Parameter | Default | Range |
|-----------|---------|-------|
| Cycle Length | 28 days | 21-45 days (dynamic) |
| Period Length | 5 days | Learned from data |
| Luteal Phase | 14 days | 10-16 days (learned) |
| Fertile Window | 6 days | Ovulation -5 to +1 |
| Min Cycles for Prediction | 3 | - |

---

## 🚀 Quick Start

### Prerequisites
- JDK 17 (Eclipse Adoptium JDK 17 recommended)
- Gradle 8.9 (Gradle Wrapper included)
- Android SDK 35

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

# Run unit tests
.\gradlew.bat testDebugUnitTest
```

### Release Signing Configuration

Already configured in `app/build.gradle`:
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

> **Note**: Copy `local.properties.example` to `local.properties` and fill in your signing credentials. Never commit `local.properties` to version control.

---

## 🛣️ Development Roadmap

> 📋 See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

- [x] 1. Project initialization & architecture setup
- [x] 2. Room database & local storage
- [x] 3. Terms of service & app lock
- [x] 4. Cycle recording features
- [x] 5. Prediction engine (adaptive statistical algorithms)
- [x] 6. Data analysis & statistics
- [x] 7. Reminder system (WorkManager)
- [x] 8. Multi-language support (7 languages + RTL)
- [x] 9. Theme customization (HSL color engine) & dark mode
- [x] 10. Recycle bin, encrypted backup & CSV export
- [x] 11. BMI calculation & tracking
- [x] 12. SQLCipher AES-256 database encryption
- [x] 13. Prediction algorithm optimization (luteal phase learning, exponential decay)
- [x] 14. Security audit & vulnerability fixes
- [ ] 15. Symptom chart visualization (MPAndroidChart)
- [ ] 16. Home screen widget
- [ ] 17. Integration research with similar open source projects

---

## 📦 Project Structure

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12 screens
│   │   │   │   ├── theme/        # Material3 theme + HSL utils
│   │   │   │   └── navigation/   # NavHost
│   │   │   ├── viewmodel/     # 9 ViewModels
│   │   │   ├── data/
│   │   │   │   ├── model/      # Data models
│   │   │   │   ├── local/      # Room DAO + Database + EncryptionKeyManager
│   │   │   │   └── repository/ # Repository layer
│   │   │   ├── utils/         # Utilities (PredictionEngine, BackupManager, etc.)
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
