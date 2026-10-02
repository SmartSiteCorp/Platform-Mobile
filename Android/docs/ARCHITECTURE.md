# SmartSite Android — Architecture

## Vue d'ensemble

Application Android native, **single activity**, Jetpack Compose + Material 3,
minSdk 26, compileSdk 37. L'alpha est entièrement hors-ligne : la couche de
données est en mémoire (aucune permission réseau dans le manifeste).

## Couches

```
┌────────────────────────────────────────────┐
│ ui/screens          (9 écrans Compose)     │
│ ui/components       (composants partagés)  │
├────────────────────────────────────────────┤
│ navigation          (Routes, AppShell)     │
├────────────────────────────────────────────┤
│ data/mock           (dépôts en mémoire,    │
│                      StateFlow)            │
│ data/model          (data classes, enums)  │
└────────────────────────────────────────────┘
```

### Données (remplace le SDK Base44)

Chaque entité du mockup React/Base44 a un dépôt singleton (`object`) dans
`data/mock/` exposant un `StateFlow` observable et des fonctions
`create` / `update` qui mutent la liste en mémoire. L'écran observe via
`collectAsState()` ; toute mutation est immédiatement visible dans l'UI.

| Entité mockup        | Dépôt Kotlin          |
| -------------------- | --------------------- |
| `Site`               | `SiteRepository`      |
| `Task`               | `TaskRepository`      |
| `Media`              | `MediaRepository`     |
| `Annotation`         | `AnnotationRepository`|
| `DroneSession`       | `DroneRepository`     |
| `auth.me()`          | `MockAuthRepository`  |

### Images

Aucune URL réseau : les images mock sont des drawables vectoriels dégradés
bundlés (`res/drawable/`). Les champs image du mockup (`imageUrl`, `planUrls`,
`file_url`) sont donc typés `@DrawableRes Int` (`imageRes`, `planRes`,
`fileRes`) ; les photos ajoutées localement (validation de tâche, upload média)
restent des `String` d'URI `content://`.

## Navigation

`navigation/Routes.kt` centralise les routes. `AppShell` fournit le scaffold :
top bar sauge (menu burger, logo, avatar utilisateur), drawer modal
(`#5A7A6B`) et bottom navigation blanche. Les jeux d'items par rôle sont dans
`NavConfig` (l'alpha utilise un utilisateur fixe `admin`).

## Feuille de route vers l'API

1. Extraire des interfaces de dépôt (`SiteRepository`, …) dans `data/`.
2. Implémenter des dépôts distants (Retrofit/Ktor) avec le même contrat.
3. Remplacer les singletons mock par injection de dépendances.
4. Ajouter l'authentification réelle (remplace `MockAuthRepository`) et la
   gestion multi-rôles (déjà prévue dans `NavConfig`).
