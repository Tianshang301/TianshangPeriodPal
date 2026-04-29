# TianshangPeriodPal

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── data/
│   │   │   │   ├── local/          # Room database, DAOs, encryption
│   │   │   │   ├── model/          # Data models
│   │   │   │   └── repository/     # Repository layer
│   │   │   ├── ui/
│   │   │   │   ├── components/     # Reusable UI components
│   │   │   │   ├── navigation/     # Navigation setup
│   │   │   │   ├── screens/        # Screen composables
│   │   │   │   └── theme/          # Theme and colors
│   │   │   ├── utils/              # Utilities (encryption, backup, etc.)
│   │   │   ├── viewmodel/          # ViewModels
│   │   │   ├── MainActivity.kt
│   │   │   └── PeriodPalApplication.kt
│   │   ├── res/
│   │   │   ├── values/             # Default resources
│   │   │   ├── values-zh/          # Chinese
│   │   │   ├── values-en/          # English
│   │   │   ├── values-ja/          # Japanese
│   │   │   ├── values-ko/          # Korean
│   │   │   ├── values-fr/          # French
│   │   │   ├── values-es/          # Spanish
│   │   │   └── values-ar/          # Arabic (RTL)
│   │   └── AndroidManifest.xml
│   ├── test/                       # Unit tests
│   └── androidTest/                # Instrumentation tests
├── build.gradle.kts
└── proguard-rules.pro
```

## Features

- **Offline-first**: All data stored locally, no network required
- **Encrypted database**: SQLCipher with Android Keystore
- **Biometric authentication**: Fingerprint/Face ID + PIN fallback
- **Multi-language support**: Chinese, English, Japanese, Korean, French, Spanish, Arabic
- **Period prediction**: Adaptive algorithm with symptom-based corrections
- **Data analysis**: Charts and statistics
- **Reminders**: WorkManager-based notifications
- **Backup/Export**: Encrypted backup and CSV export
- **Recycle bin**: Soft delete with 30-day retention

## Tech Stack

- Kotlin
- Jetpack Compose
- Room with SQLCipher
- MVVM + Repository pattern
- DataStore for preferences
- WorkManager for reminders
- BiometricPrompt for authentication
