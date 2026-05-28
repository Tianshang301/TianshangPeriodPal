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
[![Version](https://img.shields.io/badge/Version-2.1.0-blue.svg)](https://github.com/Tianshang301/TianshangPeriodPal/releases)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.10.00-ff69b4.svg)](https://developer.android.com/jetpack/compose)

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

### 現在の制限事項
> ⚠️ **データベース暗号化**: SQLCipherによる暗号化が検討されましたが、すべてのデバイスで最大限の安定性を確保するために実装されませんでした。データはデバイスの保護されたアプリストレージ（他のアプリからアクセス不可）に保存されますが、データベースファイル自体は暗号化されていません。つまり、攻撃者がroot権限またはフォレンジックツールを使用してデバイスに物理アクセスした場合、生のデータファイルが読み取られる可能性があります。デバイスの画面ロックを設定し、アプリ内のアプリロックを有効にすることをお勧めします。

### データの保護方法
- ✅ **完全オフライン** — ネットワークリクエストゼロ、パーミッション不要
- ✅ **Androidサンドボックス** — デバイスがroot化されない限り、他のアプリからデータが隔離されます
- ✅ **ローカルのみ保存** — データはデバイスの外に出ません
- ✅ **アプリロック** — 指紋/顔認証 + PINコード（Argon2idハッシュ、最小6桁）
- ✅ **暴力破解防止** — PIN連続入力失敗で自動ロック、待機時間が段階的に延長
- ✅ **スクリーンショット防止** — FLAG_SECUREが有効
- ✅ **ハードコードされた秘密情報なし** — ビルド認証情報はlocal.propertiesで管理（.gitignore対象）

### セキュリティのベストプラクティス
1. 強力なデバイス画面ロックを設定する — 最初の防衛線
2. アプリ内のアプリロックを有効にする — カジュアルな不正アクセスを防止
3. OSとセキュリティパッチを最新に保つ — 既知のrootエクスプロイトを修正
4. データを転送する場合はCSVエクスポートを使用し、転送後にファイルを即座に削除する

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
        storePassword '<YOUR_KEYSTORE_PASSWORD>'
        keyAlias '<YOUR_KEY_ALIAS>'
        keyPassword '<YOUR_KEY_PASSWORD>'
    }
}
```

> **注意**: `local.properties.example` を `local.properties` にコピーし、署名情報を記入してください。`local.properties` をバージョン管理にコミットしないでください。

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
