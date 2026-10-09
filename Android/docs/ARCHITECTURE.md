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
│ data/repository     (interfaces +          │
│                      locator Repositories) │
│ data/mock           (implémentations mock  │
│                      en mémoire, StateFlow)│
│ data/model          (data classes, enums)  │
└────────────────────────────────────────────┘
```

### Données (remplace le SDK Base44)

Les écrans ne parlent jamais aux mocks directement : ils passent par le
locateur `Repositories` (`data/repository/Repositories.kt`), qui expose
chaque dépôt derrière une interface (`SiteRepository`, `TaskRepository`,
`MediaRepository`, `AnnotationRepository`, `DroneRepository`,
`AuthRepository`). Les implémentations actuelles sont les singletons
`Mock*` de `data/mock/` : chacun expose un `StateFlow` observable et des
fonctions `create` / `update` qui mutent la liste en mémoire. L'écran
observe via `collectAsState()` ; toute mutation est immédiatement visible
dans l'UI. Pour brancher l'API, il suffira de remplacer les bindings de
`Repositories` par des implémentations distantes.

| Entité mockup        | Interface             | Implémentation mock      |
| -------------------- | --------------------- | ------------------------ |
| `Site`               | `SiteRepository`      | `MockSiteRepository`     |
| `Task`               | `TaskRepository`      | `MockTaskRepository`     |
| `Media`              | `MediaRepository`     | `MockMediaRepository`    |
| `Annotation`         | `AnnotationRepository`| `MockAnnotationRepository`|
| `DroneSession`       | `DroneRepository`     | `MockDroneRepository`    |
| `auth.me()`          | `AuthRepository`      | `MockAuthRepository`     |

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

1. ~~Extraire des interfaces de dépôt (`SiteRepository`, …) dans `data/`.~~
   **Fait** : interfaces dans `data/repository/`, mocks renommés `Mock*` et
   branchés derrière le locateur `Repositories`.
2. Implémenter des dépôts distants (Retrofit/Ktor) avec le même contrat.
3. Remplacer les singletons mock par injection de dépendances.
4. Ajouter l'authentification réelle (remplace `MockAuthRepository`) et la
   gestion multi-rôles (déjà prévue dans `NavConfig`).
