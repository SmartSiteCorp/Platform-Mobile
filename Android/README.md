# SmartSite — Android

Alpha Android de l'application de gestion de chantiers SmartSite.
Kotlin + Jetpack Compose (Material 3), **100 % hors-ligne** : toutes les données
proviennent de dépôts mock en mémoire (pas de backend, pas de permission réseau).

## Prérequis

- JDK 17+ (21 recommandé)
- Android SDK avec `platforms;android-36` et `build-tools;36.0.0`
- Android Studio (Rabbit ou plus récent) recommandé pour le développement

## Build

```bash
cd Android
./gradlew assembleDebug
```

L'APK debug est généré dans `app/build/outputs/apk/debug/app-debug.apk`.

## Installation / exécution

```bash
./gradlew installDebug        # sur un appareil ou émulateur connecté (adb)
```

Ou ouvrez le dossier `Android/` dans Android Studio et lancez la configuration `app`.

## Fonctionnalités (9 écrans)

- **Tableau de bord** : salutation, 4 statistiques, tâches récentes, raccourcis chantiers
- **Chantiers** : recherche + filtre statut, création de chantier (dialogue + date pickers)
- **Détail chantier** : stats par chantier, onglets Tâches / Médias / Plans
- **Tâches** : recherche + filtres statut/priorité, création de tâche
- **Détail tâche** : workflow ouvrier (démarrer → valider avec photo / signaler blocage)
- **Vue Drone** : flux vidéo simulé, télémétrie, machine à états des commandes, partage de flux
- **Médiathèque** : grille filtrable, dialogue détail, upload local
- **Annotations** : recherche, création avec sélecteur de couleur
- **Mon profil** : badge de rôle, édition téléphone/spécialité

## Structure du projet

```
app/src/main/java/com/smartsite/app/
├── MainActivity.kt            # point d'entrée (single activity)
├── navigation/                # Routes, AppShell (top bar + drawer + bottom nav), NavHost
├── data/
│   ├── model/                 # data classes + enums (Site, Task, Media, Annotation, DroneSession, User)
│   └── mock/                  # dépôts singletons en mémoire (remplacent le SDK Base44)
├── ui/
│   ├── theme/                 # palette SmartSite (sauge / orange / crème), typographie
│   ├── components/            # StatCard, StatusBadge, PriorityBadge, EmptyState, BrandLogo
│   └── screens/               # 9 écrans (dashboard, sites, sitedetail, tasks, taskdetail,
│                              #   drone, media, annotations, profile)
└── util/                      # formatage de dates, libellés de rôles
```

## Limitations connues (alpha)

- Données volatiles : toute modification est perdue à la mort du processus.
- "Capturer" (drone) et "Déconnexion" sont des stubs volontaires.
- Les photos de validation / blocage et les médias uploadés utilisent des URIs locales
  non persistées.

Voir [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) pour l'architecture et la feuille
de route vers l'intégration API.
