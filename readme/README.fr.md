<p align="center">
  <strong>🌐 Langues</strong><br>
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

**Une application de suivi du cycle menstruel entièrement hors ligne et axée sur la confidentialité**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](../LICENSE)
[![Version](https://img.shields.io/badge/Version-2.1.0-blue.svg)](https://github.com/Tianshang301/TianshangPeriodPal/releases)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-28%20(Android%209)-green.svg)](https://developer.android.com/about/versions/pie)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-brightgreen.svg)](https://developer.android.com/about/versions/15)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.24-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202025.10.00-ff69b4.svg)](https://developer.android.com/jetpack/compose)

</div>

---

## 📱 Présentation des fonctionnalités

> 💡 **Conseil** : Vous pouvez télécharger la dernière APK sur la page [Releases](https://github.com/Tianshang301/TianshangPeriodPal/releases).

### Fonctionnalités principales
| Fonction | Description |
|------|------|
| 📅 **Enregistrement** | Enregistrez les dates de début/fin, le flux, la douleur, 10 symptômes |
| 🔮 **Prédictions intelligentes** | Algorithmes statistiques pour prédire 6 cycles futurs (ovulation et période fertile) |
| 📊 **Analyse de données** | Analyse de la régularité, tendances de douleur, fréquence des symptômes |
| 🎨 **Personnalisation** | 5 thèmes roses + image de fond personnalisée (transparence réglable) |
| 🌗️ **Mode sombre** | Suivi du système ou basculement manuel |
| 🔒 **Verrouillage** | Empreinte/facial + code PIN, hachage Argon2 |
| 🌐 **Multilingue** | 7 langues supportées (中/英/日/韩/法/西/阿) |
| 💾 **Gestion données** | Sauvegarde DB (ZIP), export CSV, corbeille (nettoyage auto 30j) |
| ⚖️ **Suivi IMC** | Calcul et historique de l'IMC (normes chinoises) |
| ⏰ **Rappels** | 3 types de rappels (règles/ovulation/PMS), personnalisables |

---

## 🏗️ Architecture technique

### Modèle d'architecture
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

### Stack technique
| Catégorie | Bibliothèque/Framework | Version |
|------|----------|------|
| **Langage** | Kotlin | 1.9.20 |
| **UI** | Jetpack Compose (Material 3) | BOM 2023.10.01 |
| **Navigation** | Compose Navigation | 2.7.6 |
| **Architecture** | MVVM + Repository | - |
| **Base de données** | Room + SQLite | 2.6.1 |
| **Stockage local** | DataStore Preferences | 1.0.0 |
| **Arrière-plan** | WorkManager | 2.9.0 |
| **Biométrie** | AndroidX Biometric | 1.1.0 |
| **Chargement images** | Coil Compose | 2.5.0 |
| **Sécurité** | Argon2 (Bouncy Castle) | 1.78 |
| **Export** | Apache Commons CSV | 1.10.0 |
| **JSON** | Gson | 2.10.1 |
| **Build** | Gradle | 8.2 |
| **Compilation** | JDK | 17 |

---

## 📲 Description des écrans

### Navigation inférieure (5 onglets)

| Écran | Fonction |
|------|------|
| 📅 **Calendrier** | Affiche règles (rouge), prédictions (rose), ovulation (bleu), fertilité (bleu clair) |
| 📝 **Enregistrement** | Flux, douleur, 10 symptômes, rapport sexuel, test ovulation, glaire, température |
| 📊 **Analyse** | Statistiques, tendances, fréquences, prédictions 3 cycles |
| ⏰ **Rappels** | Activation, jours d'avance, heure pour règles/ovulation/PMS |
| 👤 **Profil** | Accès rapide : thème, langue, IMC, verrouillage, corbeille, sauvegarde |

### Écrans secondaires

- **Thème** : 5 couleurs roses + fond d'écran + transparence
- **Langue** : 7 langues, changement immédiat (AppCompatDelegate)
- **IMC** : Saisie taille/poids, historique
- **Verrou** : Configuration PIN, empreinte/facial
- **Corbeille** : Restauration des enregistrements supprimés (30j)
- **Sauvegarde** : Export ZIP / Import / Export CSV
- **Paramètres** : Verrouillage, capture d'écran

---

## 🔒 Confidentialité et sécurité

### Limitation actuelle
> ⚠️ **Chiffrement de la base de données** : Le chiffrement par SQLCipher a été envisagé mais non implémenté pour garantir une stabilité maximale sur tous les appareils. Vos données sont stockées dans le stockage protégé de l'app (inaccessible aux autres apps), mais le fichier de base de données lui-même n'est pas chiffré. Cela signifie qu'un attaquant disposant d'un accès physique à votre appareil avec des privilèges root ou des outils de forensique pourrait lire le fichier brut. Nous vous recommandons de configurer un verrouillage d'écran et d'activer le verrouillage intégré.

### Comment vos données sont protégées
- ✅ **Entièrement hors ligne** — Aucune requête réseau, aucune permission
- ✅ **Sandbox Android** — Données isolées des autres apps sauf si l'appareil est rooté
- ✅ **Stockage local uniquement** — Rien ne quitte votre appareil
- ✅ **Verrouillage** — Empreinte/facial + code PIN (Argon2id, minimum 6 chiffres)
- ✅ **Protection anti force brute** — Verrouillage automatique après tentatives échec, delai croissant
- ✅ **Anti-capture** — FLAG_SECURE activé
- ✅ **Aucun secret en dur** — Identifiants de build via local.properties (.gitignore)

### Bonnes pratiques de sécurité
1. Configurer un verrouillage d'écran fort — votre première ligne de défense
2. Activer le verrouillage intégré — empêche les accès non autorisés occasionnels
3. Maintenir votre OS et vos correctifs de sécurité à jour — corrige les failles root connues
4. Pour transférer des données, utilisez l'export CSV et supprimez le fichier immédiatement après

---

## 🚀 Démarrage rapide

### Prérequis
- JDK 17 (Eclipse Adoptium JDK 17 recommandé)
- Gradle 8.2 (inclus via Wrapper)
- Android SDK 34

### Commandes de build

```bash
# Cloner le projet
git clone https://github.com/Tianshang301/TianshangPeriodPal.git
cd TianshangPeriodPal

# Configurer JDK 17
set JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"

# Build Debug
.\gradlew.bat assembleDebug

# Build Release (signature requise)
.\gradlew.bat assembleRelease
```

### Configuration de signature

Déjà configurée dans `app/build.gradle` :
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

> **Note** : Copiez `local.properties.example` en `local.properties` et remplissez vos identifiants de signature. Ne commitez jamais `local.properties` dans le contrôle de version.

---

## 🛣️ Feuille de route

- [x] 1. Initialisation et architecture
- [x] 2. Room et stockage local
- [x] 3. Conditions d'utilisation et verrouillage
- [x] 4. Fonctionnalités d'enregistrement
- [x] 5. Moteur de prédiction (algorithmes stats)
- [x] 6. Analyse et statistiques
- [x] 7. Système de rappels (WorkManager)
- [x] 8. Multilingue (7 langues)
- [x] 9. Thèmes et mode sombre
- [x] 10. Corbeille, sauvegarde et export CSV
- [x] 11. Calcul et suivi IMC
- [x] 12. Tests et corrections de bugs
- [ ] 13. Visualisation graphique (MPAndroidChart)
- [ ] 14. Widget écran d'accueil
- [ ] 15. Recherche d'intégration de projets open source

---

## 📦 Structure du projet

```
TianshangPeriodPal/
├── app/
│   ├── src/main/
│   │   ├── java/com/tianshang/periodpal/
│   │   │   ├── ui/
│   │   │   │   ├── screens/      # 12 écrans
│   │   │   │   ├── theme/        # Thème Material3
│   │   │   │   └── navigation/   # NavHost
│   │   │   ├── viewmodel/     # 9 ViewModels
│   │   │   ├── data/
│   │   │   │   ├── model/      # Modèles
│   │   │   │   ├── local/      # Room DAO + Database
│   │   │   │   └── repository/ # Couche Repository
│   │   │   ├── utils/         # Utilitaires
│   │   │   └── service/       # Services arrière-plan
│   │   └── res/
│   │       ├── values/        # Anglais
│   │       ├── values-fr/     # Français
│   │       ├── values-zh/     # Chinois simplifié
│   │       ├── values-ja/     # Japonais
│   │       ├── values-ko/     # Coréen
│   │       ├── values-es/     # Espagnol
│   │       └── values-ar/     # Arabe
│   └── build.gradle
├── build.gradle
├── gradle.properties
└── README.md
```

---

## 🤝 Contribution

Les Issues et Pull Requests sont les bienvenus !

---

## 📄 Licence

Ce projet est sous licence [MIT License](../LICENSE).

---

<div align="center">
  <p>🌸 Made with ❤️ for menstrual health awareness 🌸</p>
</div>
