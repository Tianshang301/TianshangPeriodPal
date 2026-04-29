<p align="center">
  <strong>🌐 Langues</strong><br>
  <a href="../README.md">English</a> ·
  <a href="README.zh_CN.md">简体中文</a> ·
  <a href="README.ja.md">日本語</a> ·
  <a href="README.ko.md">한국어</a> ·
  <a href="README.fr.md">Français</a> ·
  <a href="README.es.md">Español</a>·
  <a href="README.ar.md">العربية</a>
</p>

---

# TianshangPeriodPal（天殇·月記）

## Présentation

Une application de suivi et de gestion du cycle menstruel, entièrement hors ligne et open source. Grâce à une architecture zéro réseau, vos données restent toujours entre vos mains.

## Stack technique

- **Langage/Framework** : Kotlin, Jetpack Compose (Material 3)
- **Base de données** : Room (SQLite local)
- **Architecture** : MVVM + Repository
- **SDK minimum** : 28 (Android 9), cible 34

## Fonctionnalités principales

- Enregistrement et prédiction du cycle menstruel
- Prédiction de l'ovulation et de la période fertile
- Analyse des douleurs menstruelles et des symptômes
- Stockage de données en local
- Verrouillage biométrique de l'application
- Support multilingue (7 langues)
- Sauvegarde et exportation des données
- Corbeille

## Fonctionnalités de confidentialité

- Entièrement hors ligne — aucune requête réseau
- Verrouillage par empreinte digitale / mot de passe
- Toutes les données stockées uniquement sur l'appareil
- Note : le chiffrement SQLCipher a été envisagé à l'origine mais ultimatement abandonné ; les données sont stockées en SQLite local en clair.

## Feuille de route

1. ✅ Initialisation du projet
2. ✅ Mise en place de la base de données
3. ✅ Conditions d'utilisation et verrouillage
4. ✅ Fonctionnalités d'enregistrement
5. ✅ Moteur de prédiction
6. ✅ Analyse des données
7. ✅ Système de rappels
8. ✅ Multilingue et thèmes
9. ✅ Corbeille, sauvegarde et exportation
10. ✅ Tests

## Licence

Ce projet est sous licence [MIT License](../LICENSE).
