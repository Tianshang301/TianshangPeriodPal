# Changelog

All notable changes to TianshangPeriodPal (天殇·月记) will be documented in this file.

---

## [1.5.0] - 2026-05-26

### Version Fork

- Forked from master@1.4.0 as stable maintenance branch.
- Plain SQLite database (no encryption migration).

### UI Redesign

- New cute-style UI components: BouncyButton, CartoonCalendarDay, ColorPickerDialog, EmptyStateCharacter
- Added Shape.kt for rounded corner theme support
- Enhanced ThemeScreen with improved color picker and background customization
- Updated AnalysisScreen, CalendarScreen, RecycleBinScreen with new component integration
- Updated multi-language strings for all 7 languages (zh/en/ja/ko/fr/es/ar)

---

## [1.4.0] - 2026-05-25

### Security Hardening (Per SECURITY.md Audit)

This release addresses a comprehensive security audit based on the project's SECURITY.md policy. **28 files changed, +251 / -162 lines.**

#### Critical Fixes
- **Remove hardcoded signing passwords** from `app/build.gradle` and all 7 README files. Signing credentials are now read exclusively from `local.properties` (git-ignored). A `local.properties.example` template is provided.
- **Remove Argon2 fallback** to reversible Base64 encoding in `EncryptionManager`. If Argon2 hashing fails, the operation now throws instead of silently storing a weak, reversible hash.
- **Remove duplicate password hash** stored in plain DataStore (`SettingsRepository`). App lock password hashes are now stored only in `EncryptedSharedPreferences`.

#### High Priority
- **Brute-force protection** on PIN entry with exponential lockout: 3 failed attempts → 5s delay, 5 → 30s, 10 → 5-minute lockout. PIN minimum length increased from 4 to 6 digits.
- **File size validation** (10MB limit) and magic-byte image type checks (JPEG/PNG/WebP) for background image import in `SettingsViewModel`.

#### Medium Priority
- **Fix ZIP Slip** path traversal vulnerability in `BackupManager.importDatabase()`. ZIP entry names are now sanitized and canonical paths validated.
- **Remove all `printStackTrace()`** calls from `BackupManager`, `ExportManager`, and `SettingsViewModel`. Errors are now handled silently without leaking stack traces to logcat.
- **Add input validation**: BMI height (10–300cm), weight (5–500kg); body temperature range (30–45°C); text field length limits (ovulation test: 200, cervical mucus: 200, notes: 500).
- **BootReceiver** changed from `exported="true"` to `exported="false"` in `AndroidManifest.xml`.

#### Low Priority
- **Error messages** now use generic string resources instead of exposing exception details. Added `auth_failed_generic`, `biometric_init_failed`, and `lockout_message` strings in all 8 languages.

#### Other
- Removed stale `app/src/build.gradle` duplicate file.
- Build verified on Huawei device: install, launch, all 5 tabs navigation — zero crashes.

---

## [1.3.0] - 2026-05-03

### Features & Improvements
- Implement `ReminderScheduler.scheduleReminders()` with PredictionEngine integration
- Implement `BackupViewModel.importDatabase()` with file picker
- Add notification icon (`ic_notification.xml`) and notification channels on app startup
- Replace hardcoded Chinese strings in PredictionEngine with enum constants (ConfidenceLevel, CycleRegularity, OvulationTestResult, CervicalMucusType)
- Remove unused SQLCipher ProGuard rule
- AppLock: PIN length restriction (4–8 digits) with character counter
- AnalysisScreen: show guidance prompt when no data available
- BmiScreen: label "Based on Chinese BMI standard"
- BackupScreen: add description text for each button
- CalendarScreen: add "Today" button to jump back to current month
- Notification channel names use localized strings
- Add strings in 8 languages (zh, en, ja, ko, fr, es, ar) for: notifications, analysis_no_data, pin_length_error, bmi_standard_note, backup descriptions, calendar_today
- Version: 1.2.2 → 1.3.0 (versionCode 5 → 6)

---

## [1.2.2] - 2026-04-XX

- Bug fixes and stability improvements

---

## [1.2.0] - 2026-04-XX

- BMI calculation and tracking
- Recycle bin with 30-day auto-cleanup
- Database backup/restore (ZIP)
- CSV data export

---

## [1.0.0] - 2026-03-XX

- Initial release
- Cycle recording and prediction
- Data analysis and statistics
- Reminder system (WorkManager)
- Multi-language support (7 languages)
- Theme customization and dark mode
- App lock with biometrics + PIN
