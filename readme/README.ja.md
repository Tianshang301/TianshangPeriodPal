<p align="center">
  <strong>🌐 言語</strong><br>
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

# 🌸 TianshangPeriodPal（天殇·月記）

**完全オフライン、プライバシー重視の月経周期トラッキング＆管理アプリ**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 機能プレビュー

> 💡 **ヒント**：最新の APK は [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) ページからダウンロードできます。

### コア機能
| 機能 | 説明 |
|------|------|
| 📅 **周期記録** | 月経開始/終了日、経血量、痛みの程度、10種類の症状を記録 |
| 🔮 **スマート予測** | 統計アルゴリズムで将来6周期を予測（排卵期・受孕期含む） |
| 📊 **データ分析** | 周期の規則性分析、痛みの傾向、症状頻度統計 |
| 🎨 **テーマカスタマイズ** | 5色のピンク系テーマ ＋ カスタム背景画像（透明度調整可）|
| 🌗️ **ダークモード** | システム追従または手動切替 |
| 🔒 **アプリロック** | 指紋/顔認証 ＋ PIN コード、Argon2 ハッシュ暗号化 |
| 🌐 **多言語** | 7言語対応（中/英/日/韓/仏/西/阿）|
| 💾 **データ管理** | データベースバックアップ(ZIP)、CSV出力、ゴミ箱(30日自動消去) |
| ⚖️ **BMI 追跡** | BMI 計算と履歴追跡（中国基準）|
| ⏰ **リマインダー** | 月経/排卵/PMS の3種類リマインダー、日数と時間をカスタマイズ可 |

---

## 🏗️ 技術アーキテクチャ

### アーキテクチャパターン
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

### 技術スタック
| カテゴリ | ライブラリ/フレームワーク | バージョン |
|------|----------|------|
| **言語** | Kotlin | 1.9.20 |
| **UI フレームワーク** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **ナビゲーション** | Compose Navigation | 2.7.6 |
| **アーキテクチャ** | MVVM + Repository | - |
| **データベース** | Room + SQLite | 2.6.1 |
| **ローカルストレージ** | DataStore Preferences | 1.0.0 |
| **バックグラウンド** | WorkManager | 2.9.0 |
| **生体認証** | AndroidX Biometric | 1.1.0 |
| **画像ロード** | Coil Compose | 2.5.0 |
| **データセキュリティ** | Argon2 (Bouncy Castle) | 1.78 |
| **データ出力** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **ビルドツール** | Gradle | 8.2 |
| **コンパイル** | JDK | 17 |

---

## 📲 画面説明

### ボトムナビゲーション（5タブ）

| 画面 | 機能 |
|------|------|
| 📅 **カレンダー** | 月経（赤）、予測（薄赤）、排卵（青）、受孕期（薄青）を表示；日付タップで記録 |
| 📝 **記録** | 経血量、痛み、10症状、性生活、排卵テスト、頸管粘液、体温を記録 |
| 📊 **分析** | 周期統計、痛み傾向グラフ、症状頻度、将来3周期予測 |
| ⏰ **リマインダー** | 月経/排卵/PMS リマインダーのON/OFF、日数、時間設定 |
| 👤 **プロフィール** | テーマ、言語、BMI、アプリロック、ゴミ箱、バックアップへの快捷入口 |

### サブ画面

- **テーマカスタマイズ**：5色のピンク系 ＋ カスタム背景 ＋ 透明度調整
- **言語選択**：7言語即時切替（AppCompatDelegate）
- **BMI 計算**：身長・体重入力、履歴表示
- **アプリロック**：初回PIN設定、指紋/顔認証対応
- **ゴミ箱**：30日以内に削除された記録を復元
- **バックアップ**：ZIP 出力 / インポート / CSV 出力
- **設定**：アプリロック、スクリーンショット防止

---

## 🔒 プライバシーとセキュリティ

### プライバシー機能
- ✅ **完全オフライン** — ネットワークリクエストゼロ、パーミッション不要
- ✅ **ローカルストレージ** — すべてのデータはデバイスにのみ保存
- ✅ **アプリロック** — 指紋/顔認証 ＋ PIN コード
- ✅ **スクリーンショット防止** — FLAG_SECURE でスクリーンショット禁止可

### 暗号化について
> ⚠️ **注意**：プロジェクト初期は SQLCipher によるデータベース暗号化を検討しましたが、最終的に放棄しました。現在、データは**平文（プレーンテキスト）**形式でローカル SQLite データベースに保存されています。アプリロックはアプリへのアクセスを保護しますが、データベースファイル自体は暗号化されません。

### データセキュリティ推奨
1. アプリロック機能を有効にする
2. 定期的にバックアップ機能でデータをエクスポートする
3. デバイスに画面ロックを設定する

---

## 🚀 クイックスタート

### 必要条件
- JDK 17（Eclipse Adoptium JDK 17 推奨）
- Gradle 8.2（プロジェクトに Gradle Wrapper 同梱）
- Android SDK 34

### ビルドコマンド

```bash
# プロジェクトをクローン
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# JDK 17 を設定
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# Debug 版をビルド
.\gradlew.bat assembleDebug

# Release 版をビルド（署名設定が必要）
.\gradlew.bat assembleRelease
```

### Release 署名設定

`app/build.gradle` で署名が設定済み：
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

## 🛣️ 開発ロードマップ

- [x] 1. プロジェクト初期化とアーキテクチャ構築
- [x] 2. Room データベースとローカルストレージ
- [x] 3. 利用規約とアプリロック
- [x] 4. 周期記録機能
- [x] 5. 予測エンジン（統計アルゴリズム）
- [x] 6. データ分析と統計
- [x] 7. リマインダーシステム（WorkManager）
- [x] 8. 多言語対応（7言語）
- [x] 9. テーマカスタマイズとダークモード
- [x] 10. ゴミ箱、バックアップと CSV 出力
- [x] 11. BMI 計算と追跡
- [x] 12. テストとバグ修正
- [ ] 13. 月経症状チャート可視化（MPAndroidChart）
- [ ] 14. ホーム画面ウィジェット
- [ ] 15. 同名オープンソースプロジェクト統合研究

---

## 📦 プロジェクト構造

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12の画面
│   │   │   │   ├── theme/        # Material3 テーマ
│   │   │   │   └── navigation/   # NavHost ナビゲーション
│   │   │   ├── viewmodel/     # 9つの ViewModel
│   │   │   ├── data/
│   │   │   │   ├── model/      # データモデル
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Repository 層
│   │   │   ├── utils/         # ユーティリティ
│   │   │   └── service/       # バックグラウンドサービス
│   │   └── res/
│   │       ├── values/        # 英語文字列
│   │       ├── values-ja/     # 日本語
│   │       ├── values-zh/     # 簡体中文
│   │       ├── values-ko/     # 韓国語
│   │       ├── values-fr/     # フランス語
│   │       ├── values-es/     # スペイン語
│   │       └── values-ar/     # アラビア語
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 コントリビューション

Issue や Pull Request を歓迎します！

---

## 📄 ライセンス

このプロジェクトは [MIT License](../LICENSE) の下で公開されています。

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
