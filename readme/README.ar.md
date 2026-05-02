<p align="right">
  <strong>🌐 اللغات</strong><br>
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

# 🌸 TianshangPeriodPal (تيانشانغ·يوه جي)

**تطبيق لتتبع الدورة الشهرية يعمل بالكامل دون اتصال ويركز على الخصوصية**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 نظرة عامة على الميزات

> 💡 **تلميح**: يمكنك تنزيل أحدث APK من صفحة [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases).

### الميزات الأساسية
| الميزة | الوصف |
|------|------|
| 📅 **تسجيل** | سجل تواريخ البدء والانتهاء، التدفق، الألم، 10 أعراض |
| 🔮 **توقعات ذكية** | خوارزميات إحصائية للتوقع 6 دورات مستقبلية (التبويض والفترة الخصبة) |
| 📊 **تحليل البيانات** | تحليل الانتظام، توجهات الألم، تكرار الأعراض |
| 🎨 **تخصيص** | 5 سمات وردية + صورة خلفية مخصصة (شفافية قابلة للتعديل) |
| 🌗️ **الوضع المظلم** | اتباع النظام أو التبديل اليدوي |
| 🔒 **قفل التطبيق** | بصمة/وجه + رمز PIN، تجزئة Argon2 |
| 🌐 **متعدد اللغات** | دعم 7 لغات (中/英/日/韩/法/西/阿) |
| 💾 **إدارة البيانات** | نسخ احتياطي (ZIP)، تصدير CSV، سلة المحذوفات (تنظيف تلقائي 30ي) |
| ⚖️ **تتبع مؤشر كتلة الجسم** | حساب ومتابعة مؤشر كتلة الجسم (معايير صينية) |
| ⏰ **التذكيرات** | 3 أنواع (الدورة/التبويض/PMS)، قابلة للتخصيص |

---

## 🏗️ البنية التقنية

### نمط البنية
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

### الرصة التقنية
| الفئة | المكتبة/الإطار | الإصدار |
|------|----------|------|
| **اللغة** | Kotlin | 1.9.20 |
| **واجهة المستخدم** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **التنقل** | Compose Navigation | 2.7.6 |
| **البنية** | MVVM + Repository | - |
| **قاعدة البيانات** | Room + SQLite | 2.6.1 |
| **التخزين المحلي** | DataStore Preferences | 1.0.0 |
| **الخلفية** | WorkManager | 2.9.0 |
| **القياسات الحيوية** | AndroidX Biometric | 1.1.0 |
| **تحميل الصور** | Coil Compose | 2.5.0 |
| **الأمن** | Argon2 (Bouncy Castle) | 1.78 |
| **التصدير** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **البناء** | Gradle | 8.2 |
| **التجميع** | JDK | 17 |

---

## 📲 وصف الشاشات

### التنقل السفلي (5 علامات تبويب)

| الشاشة | الوظيفة |
|------|------|
| 📅 **التقويم** | يعرض الدورة (أحمر)، التوقعات (وردي)، التبويض (أزرق)، الخصب (أزرق فاتح) |
| 📝 **التسجيل** | التدفق، الألم، 10 أعراض، العلاقات، اختبار التبويض، الإفرازات، الحرارة |
| 📊 **التحليل** | الإحصائيات، التوجهات، التكرار، توقعات 3 دورات |
| ⏰ **التذكيرات** | تفعيل، أيام مسبقة، وقت للدورة/التبويض/PMS |
| 👤 **الملف** | وصول سريع: السمة، اللغة، مؤشر كتلة الجسم، القفل، السلة، النسخ |

### الشاشات الثانوية

- **السمة**: 5 ألوان وردية + خلفية + شفافية
- **اللغة**: 7 لغات، تبديل فوري (AppCompatDelegate)
- **مؤشر كتلة الجسم**: الطول/الوزن، التاريخ
- **قفل التطبيق**: إعداد PIN، بصمة/وجه
- **سلة المحذوفات**: استعادة السجلات المحذوفة (30ي)
- **النسخ الاحتياطي**: تصدير ZIP / استيراد / CSV
- **الإعدادات**: القفل، منع لقطات الشاشة

---

## 🔒 الخصوصية والأمان

### ميزات الخصوصية
- ✅ **يعمل بالكامل دون اتصال** — صفر طلبات شبكة، صفر أذونات
- ✅ **تخزين محلي** — جميع البيانات على جهازك فقط
- ✅ **قفل التطبيق** — بصمة/وجه + رمز PIN
- ✅ **منع اللقطات** — FLAG_SECURE لمنع لقطات الشاشة

### حول التشفير
> ⚠️ **ملاحظة**: تم النظر في تشفير SQLCipher في البداية ولكن تم التخلي عنه في النهاية. يتم الآن تخزين البيانات في **نص عادي** في قاعدة بيانات SQLite المحلية. قفل التطبيق يحمي الوصول للتطبيق ولكنه لا يشفر قاعدة البيانات.

### توصيات الأمان
1. فعل قفل التطبيق
2. صدر البيانات بانتظام
3. أعد ضبط قفل الشاشة على الجهاز

---

## 🚀 البدء السريع

### المتطلبات
- JDK 17 (يُنصح بـ Eclipse Adoptium JDK 17)
- Gradle 8.2 (مشمول عبر Wrapper)
- Android SDK 34

### أوامر البناء

```bash
# استنساخ المشروع
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# إعداد JDK 17
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# بناء نسخة Debug
.\gradlew.bat assembleDebug

# بناء نسخة Release (يتطلب توقيع)
.\gradlew.bat assembleRelease
```

### إعداد التوقيع

تم الإعداد مسبقاً في `app/build.gradle`:
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

## 🛣️ خارطة الطريق

- [x] 1. تهيئة المشروع والبنية
- [x] 2. Room والتخزين المحلي
- [x] 3. شروط الاستخدام والقفل
- [x] 4. ميزات التسجيل
- [x] 5. محرك التوقعات (خوارزميات إحصائية)
- [x] 6. التحليل والإحصائيات
- [x] 7. نظام التذكيرات (WorkManager)
- [x] 8. متعدد اللغات (7 لغات)
- [x] 9. السمات والوضع المظلم
- [x] 10. سلة المحذوفات، النسخ الاحتياطي وتصدير CSV
- [x] 11. حساب ومتابعة مؤشر كتلة الجسم
- [x] 12. الاختبار وتصحيح الأخطاء
- [ ] 13. تصور المخططات البيانية (MPAndroidChart)
- [ ] 14. بريمج الشاشة الرئيسية
- [ ] 15. بحث دمج مشاريع مفتوحة المصدر

---

## 📦 هيكل المشروع

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12 شاشة
│   │   │   │   ├── theme/        # سمة Material3
│   │   │   │   └── navigation/   # NavHost
│   │   │   ├── viewmodel/     # 9 ViewModels
│   │   │   ├── data/
│   │   │   │   ├── model/      # النماذج
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # طبقة Repository
│   │   │   ├── utils/         # الأدوات
│   │   │   └── service/       # خدمات الخلفية
│   │   └── res/
│   │       ├── values/        # الإنجليزية
│   │       ├── values-ar/     # العربية
│   │       ├── values-zh/     # الصينية المبسطة
│   │       ├── values-ja/     # اليابانية
│   │       ├── values-ko/     # الكورية
│   │       ├── values-fr/     # الفرنسية
│   │       └── values-es/     # الإسبانية
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 المساهمة

نرحب بـ Issues و Pull Requests!

---

## 📄 الترخيص

هذا المشروع مرخص بموجب [MIT License](../LICENSE).

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
