<p align="center">
  <strong>🌐 Idiomas</strong><br>
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

**Una aplicación de seguimiento del ciclo menstrual completamente offline y centrada en la privacidad**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-34%20(Android%2014)-brightgreen.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.20-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 Vista previa de funciones

> 💡 **Consejo**: Puede descargar la última APK en la página de [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases).

### Funciones principales
| Función | Descripción |
|------|------|
| 📅 **Registro** | Registre fechas de inicio/fin, flujo, dolor, 10 síntomas |
| 🔮 **Predicciones** | Algoritmos estadísticos para predecir 6 ciclos futuros (ovulación y período fértil) |
| 📊 **Análisis** | Análisis de regularidad, tendencias de dolor, frecuencia de síntomas |
| 🎨 **Personalización** | 5 temas rosas + fondo personalizado (transparencia ajustable) |
| 🌗️ **Modo oscuro** | Seguir sistema o cambio manual |
| 🔒 **Bloqueo** | Huella/facial + PIN, hash Argon2 |
| 🌐 **Multilingüe** | 7 idiomas soportados (中/英/日/韩/法/西/阿) |
| 💾 **Gestión de datos** | Copia de seguridad DB (ZIP), exportar CSV, papelera (limpieza auto 30d) |
| ⚖️ **Seguimiento IMC** | Cálculo e historial del IMC (normas chinas) |
| ⏰ **Recordatorios** | 3 tipos (regla/ovulación/PMS), personalizables |

---

## 🏗️ Arquitectura técnica

### Patrón de arquitectura
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

### Stack tecnológico
| Categoría | Biblioteca/Framework | Versión |
|------|----------|------|
| **Lenguaje** | Kotlin | 1.9.20 |
| **UI** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **Navegación** | Compose Navigation | 2.7.6 |
| **Arquitectura** | MVVM + Repository | - |
| **Base de datos** | Room + SQLite | 2.6.1 |
| **Almacenamiento** | DataStore Preferences | 1.0.0 |
| **Segundo plano** | WorkManager | 2.9.0 |
| **Biometría** | AndroidX Biometric | 1.1.0 |
| **Carga imágenes** | Coil Compose | 2.5.0 |
| **Seguridad** | Argon2 (Bouncy Castle) | 1.78 |
| **Exportar** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **Build** | Gradle | 8.2 |
| **Compilación** | JDK | 17 |

---

## 📲 Descripción de pantallas

### Navegación inferior (5 pestañas)

| Pantalla | Función |
|------|------|
| 📅 **Calendario** | Muestra regla (rojo), predicciones (rosa), ovulación (azul), fértil (azul claro) |
| 📝 **Registro** | Flujo, dolor, 10 síntomas, relaciones, test ovulación, moco, temperatura |
| 📊 **Análisis** | Estadísticas, tendencias, frecuencias, predicciones 3 ciclos |
| ⏰ **Recordatorios** | Activar, días anticipados, hora para regla/ovulación/PMS |
| 👤 **Perfil** | Acceso rápido: tema, idioma, IMC, bloqueo, papelera, copia |

### Pantallas secundarias

- **Tema**: 5 colores rosas + fondo + transparencia
- **Idioma**: 7 idiomas, cambio inmediato (AppCompatDelegate)
- **IMC**: Altura/peso, historial
- **Bloqueo**: Configurar PIN, huella/facial
- **Papelera**: Restaurar registros eliminados (30d)
- **Copia de seguridad**: Exportar ZIP / Importar / CSV
- **Ajustes**: Bloqueo, captura de pantalla

---

## 🔒 Privacidad y seguridad

### Funciones de privacidad
- ✅ **Completamente offline** — cero peticiones de red, cero permisos
- ✅ **Almacenamiento local** — todos los datos solo en tu dispositivo
- ✅ **Bloqueo** — huella/facial + código PIN
- ✅ **Anti-captura** — FLAG_SECURE para evitar capturas

### Sobre el cifrado
> ⚠️ **Nota**: Se consideró el cifrado SQLCipher al principio pero se abandón. Los datos se almacenan ahora en **texto plano** en la base SQLite local. El bloqueo protege el acceso a la app pero no cifra la base de datos.

### Recomendaciones
1. Activar el bloqueo de la aplicación
2. Exportar datos regularmente
3. Configurar bloqueo de pantalla en el dispositivo

---

## 🚀 Inicio rápido

### Requisitos
- JDK 17 (Eclipse Adoptium JDK 17 recomendado)
- Gradle 8.2 (incluido via Wrapper)
- Android SDK 34

### Comandos de build

```bash
# Clonar proyecto
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# Configurar JDK 17
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# Build Debug
.\gradlew.bat assembleDebug

# Build Release (requiere firma)
.\gradlew.bat assembleRelease
```

### Configuración de firma

Ya configurada en `app/build.gradle`:
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

## 🛣️ Hoja de ruta

- [x] 1. Inicialización y arquitectura
- [x] 2. Room y almacenamiento local
- [x] 3. Términos de uso y bloqueo
- [x] 4. Funciones de registro
- [x] 5. Motor de predicción (algoritmos estadísticos)
- [x] 6. Análisis y estadísticas
- [x] 7. Sistema de recordatorios (WorkManager)
- [x] 8. Multilingüe (7 idiomas)
- [x] 9. Temas y modo oscuro
- [x] 10. Papelera, copia de seguridad y exportar CSV
- [x] 11. Cálculo y seguimiento IMC
- [x] 12. Pruebas y corrección de errores
- [ ] 13. Visualización de gráficos (MPAndroidChart)
- [ ] 14. Widget de pantalla de inicio
- [ ] 15. Investigación de integración de proyectos de código abierto

---

## 📦 Estructura del proyecto

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12 pantallas
│   │   │   │   ├── theme/        # Tema Material3
│   │   │   │   └── navigation/   # NavHost
│   │   │   ├── viewmodel/     # 9 ViewModels
│   │   │   ├── data/
│   │   │   │   ├── model/      # Modelos
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Capa Repository
│   │   │   ├── utils/         # Utilidades
│   │   │   └── service/       # Servicios en segundo plano
│   │   └── res/
│   │       ├── values/        # Ingés
│   │       ├── values-es/     # Español
│   │       ├── values-zh/     # Chino simplificado
│   │       ├── values-ja/     # Japonés
│   │       ├── values-ko/     # Coreano
│   │       ├── values-fr/     # Francés
│   │       └── values-ar/     # Árabe
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 Contribuir

¡Las Issues y Pull Requests son bienvenidas!

---

## 📄 Licencia

Este proyecto está bajo la [MIT License](../LICENSE).

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
