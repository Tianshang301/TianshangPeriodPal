<p align="center">
  <strong>🌐 언어</strong><br>
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

**완전 오프라인, 개인정보 보호 중심의 월경 주기 추적 및 관리 앱**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Version](https://img.shields.io/badge/Version-2.1.0-blue.svg)](https://github.com/Tianshang301/TianshangPeriodPal/releases)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.10.00-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 기능 미리보기

> 💡 **팁**：최신 APK는 [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases) 페이지에서 다운로드할 수 있습니다.

### 핵심 기능
| 기능 | 설명 |
|------|------|
| 📅 **주기 기록** | 월경 시작/종료일, 혈流량, 통증 정도, 10가지 증상 기록 |
| 🔮 **스마트 예측** | 통계 알고리즘으로 향후 6주기 예측 (배란기 및 가임기 포함) |
| 📊 **데이터 분석** | 주기 규칙성 분석, 통증 경향, 증상 빈도 통계 |
| 🎨 **테마 커스터마이징** | 5가지 핑크 계열 테마 + 커스텀 배경 이미지 (투명도 조절 가능) |
| 🌗️ **다크 모드** | 시스템 따르기 또는 수동 전환 |
| 🔒 **앱 잠금** | 지문/얼굴 인식 + PIN 코드, Argon2 해시 암호화 |
| 🌐 **다국어** | 7개 언어 지원 (중/영/일/한/불/서/아랍) |
| 💾 **데이터 관리** | 데이터베이스 백업(ZIP), CSV 내보내기, 휴지통 (30일 자동 삭제) |
| ⚖️ **BMI 추적** | BMI 계산 및 기록 추적 (중국 기준) |
| ⏰ **리마인더** | 월경/배란/PMS 3가지 리마인더, 사전 일수 및 시간 커스터마이징 |

---

## 🏗️ 기술 아키텍처

### 아키텍처 패턴
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

### 기술 스택
| 카테고리 | 라이브러리/프레임워크 | 버전 |
|------|----------|------|
| **언어** | Kotlin | 1.9.20 |
| **UI 프레임워크** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **내비게이션** | Compose Navigation | 2.7.6 |
| **아키텍처** | MVVM + Repository | - |
| **데이터베이스** | Room + SQLite | 2.6.1 |
| **로컬 스토리지** | DataStore Preferences | 1.0.0 |
| **백그라운드** | WorkManager | 2.9.0 |
| **생체 인식** | AndroidX Biometric | 1.1.0 |
| **이미지 로딩** | Coil Compose | 2.5.0 |
| **데이터 보안** | Argon2 (Bouncy Castle) | 1.78 |
| **데이터 내보내기** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **빌드 도구** | Gradle | 8.2 |
| **컴파일** | JDK | 17 |

---

## 📲 화면 설명

### 하단 내비게이션 (5개 탭)

| 화면 | 기능 |
|------|------|
| 📅 **캘린더** | 월경(빨강), 예측(연한 빨강), 배란(파랑), 가임기(연한 파랑) 표시; 날짜 탭하여 기록 |
| 📝 **기록** | 혈流량, 통증, 10가지 증상, 성생활, 배란 테스트, 자궁 경부 점액, 체온 기록 |
| 📊 **분석** | 주기 통계, 통증 경향 차트, 증상 빈도, 향후 3주기 예측 |
| ⏰ **리마인더** | 월경/배란/PMS 리마인더 ON/OFF, 사전 일수 및 시간 설정 |
| 👤 **프로필** | 테마, 언어, BMI, 앱 잠금, 휴지통, 백업 바로가기 |

### 하위 화면

- **테마 커스터마이징**: 5가지 핑크 테마 + 커스텀 배경 + 투명도 조절
- **언어 선택**: 7개 언어 즉시 전환 (AppCompatDelegate)
- **BMI 계산**: 키/몸무 입력, 기록 확인
- **앱 잠금**: 첫 회 PIN 설정, 지문/얼굴 인식 지원
- **휴지통**: 30일 이내 삭제된 기록 복원
- **백업**: ZIP 내보내기 / 가져오기 / CSV 내보내기
- **설정**: 앱 잠금, 스크린샷 방지

---

## 🔒 개인정보 보호 및 보안

### 현재 제한 사항
> ⚠️ **데이터베이스 암호화**: SQLCipher 암호화가 검토되었으나, 모든 기기에서 최대 안정성을 보장하기 위해 구현되지 않았습니다. 데이터는 기기의 보호된 앱 저장소에 저장되지만(다른 앱이 접근 불가), 데이터베이스 파일 자체는 암호화되지 않습니다. 즉, 공격자가 루트 권법이나 포렌식 도구를 사용하여 기기에 물리적으로 접근하면 원본 데이터 파일을 읽을 수 있습니다. 기기 화면 잠금을 설정하고 앱 내 앱 잠금을 활성화하는 것을 권장합니다.

### 데이터 보호 방식
- ✅ **완전 오프라인** — 네트워크 요청 없음, 권한 불필요
- ✅ **Android 샌드박스 격리** — 기기가 루팅되지 않는 한 다른 앱에서 데이터가 격리됩니다
- ✅ **로컬 전용 저장** — 데이터가 기기 밖으로 나가지 않습니다
- ✅ **앱 잠금** — 지문/얼굴 인식 + PIN 코드 (Argon2id 해시, 최소 6자리)
- ✅ **무차별 대입 공격 방지** — PIN 연속 입력 실패 시 자동 잠금, 대기 시간 단계적 증가
- ✅ **스크린샷 방지** — FLAG_SECURE 활성화
- ✅ **하드코딩된 비밀 없음** — 빌드 인증 정보는 local.properties로 관리 (.gitignore 적용)

### 보안 모범 사례
1. 강력한 기기 화면 잠금 설정 — 첫 번째 방어선
2. 앱 내 앱 잠금 활성화 — 무단 접근 방지
3. OS 및 보안 패치 업데이트 유지 — 알려진 루팅 익스플로잇 패치
4. 데이터 전송이 필요한 경우 CSV 내보내기 사용 후 전송 즉시 파일 삭제

---

## 🚀 빠른 시작

### 필수 조건
- JDK 17 (Eclipse Adoptium JDK 17 권장)
- Gradle 8.2 (프로젝트에 Gradle Wrapper 포함)
- Android SDK 34

### 빌드 명령어

```bash
# 프로젝트 클론
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# JDK 17 설정
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# Debug 버전 빌드
.\gradlew.bat assembleDebug

# Release 버전 빌드 (서명 설정 필요)
.\gradlew.bat assembleRelease
```

### Release 서명 설정

`app/build.gradle`에 서명이 설정되어 있습니다:
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

> **참고**: `local.properties.example`을 `local.properties`로 복사하고 서명 정보를 입력하세요. `local.properties`를 버전 관리에 커밋하지 마세요.

---

## 🛣️ 개발 로드맵

- [x] 1. 프로젝트 초기화 및 아키텍처 구축
- [x] 2. Room 데이터베이스 및 로컬 스토리지
- [x] 3. 이용약관 및 앱 잠금
- [x] 4. 주기 기록 기능
- [x] 5. 예측 엔진 (통계 알고리즘)
- [x] 6. 데이터 분석 및 통계
- [x] 7. 리마인더 시스템 (WorkManager)
- [x] 8. 다국어 지원 (7개 언어)
- [x] 9. 테마 커스터마이징 및 다크 모드
- [x] 10. 휴지통, 백업 및 CSV 내보내기
- [x] 11. BMI 계산 및 추적
- [x] 12. 테스트 및 버그 수정
- [ ] 13. 월경 증상 차트 시각화 (MPAndroidChart)
- [ ] 14. 홈 화면 위젯
- [ ] 15. 동명 오픈소스 프로젝트 통합 연구

---

## 📦 프로젝트 구조

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12개 화면
│   │   │   │   ├── theme/        # Material3 테마
│   │   │   │   └── navigation/   # NavHost 내비게이션
│   │   │   ├── viewmodel/     # 9개 ViewModel
│   │   │   ├── data/
│   │   │   │   ├── model/      # 데이터 모델
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Repository 층
│   │   │   ├── utils/         # 유틸리티
│   │   │   └── service/       # 백그라운드 서비스
│   │   └── res/
│   │       ├── values/        # 영어 문자열
│   │       ├── values-ko/     # 한국어
│   │       ├── values-zh/     # 간체중문
│   │       ├── values-ja/     # 일본어
│   │       ├── values-fr/     # 프랑스어
│   │       ├── values-es/     # 스페인어
│   │       └── values-ar/     # 아랍어
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 기여하기

Issue 및 Pull Request를 환영합니다!

---

## 📄 라이선스

이 프로젝트는 [MIT License](../LICENSE) 하에 오픈소스로 배포됩니다.

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
