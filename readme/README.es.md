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

# TianshangPeriodPal（天殇·月記）

## Descripción

Una aplicación de seguimiento y gestión del ciclo menstrual completamente offline y de código abierto. A través de una arquitectura de red cero, sus datos siempre permanecen en sus manos.

## Stack tecnológico

- **Lenguaje/Framework**: Kotlin, Jetpack Compose (Material 3)
- **Base de datos**: Room (SQLite local)
- **Arquitectura**: MVVM + Repository
- **SDK mínimo**: 28 (Android 9), objetivo 34

## Funciones principales

- Registro y predicción del ciclo menstrual
- Predicción de ovulación y período fértil
- Análisis de dismenorrea y síntomas
- Almacenamiento de datos en local
- Bloqueo biométrico de la aplicación
- Soporte multilingüe (7 idiomas)
- Copia de seguridad y exportación de datos
- Papelera de reciclaje

## Características de privacidad

- Completamente offline — cero peticiones de red
- Bloqueo con huella dactilar / contraseña
- Todos los datos almacenados solo en el dispositivo
- Nota: el cifrado SQLCipher se consideró inicialmente pero finalmente se abandonó; los datos se almacenan en SQLite local en texto plano.

## Hoja de ruta

1. ✅ Inicialización del proyecto
2. ✅ Configuración de la base de datos
3. ✅ Términos de uso y bloqueo de aplicación
4. ✅ Funciones de registro
5. ✅ Motor de predicción
6. ✅ Análisis de datos
7. ✅ Sistema de recordatorios
8. ✅ Multilingüe y temas
9. ✅ Papelera, copia de seguridad y exportación
10. ✅ Pruebas

## Licencia

Este proyecto está licenciado bajo la [MIT License](../LICENSE).
