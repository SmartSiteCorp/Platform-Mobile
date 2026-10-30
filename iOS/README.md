# SmartSite — iOS

Alpha iOS de l'application SmartSite, miroir de l'alpha Android : SwiftUI,
**100 % hors-ligne** (dépôts mock en mémoire, aucun appel réseau), UI en français.

## Prérequis

- macOS avec Xcode 16+
- Aucune dépendance externe (pas de SPM, pas de CocoaPods)

## Ouvrir / lancer

1. Ouvrir `iOS/SmartSite.xcodeproj` dans Xcode
2. Sélectionner le scheme **SmartSite** et un simulateur (iOS 17+)
3. ⌘R

Le projet utilise les groupes synchronisés Xcode 16 : tout fichier `.swift`
ajouté sous `SmartSite/` est compilé automatiquement, sans éditer le projet.

## Structure

```
SmartSite/
├── SmartSiteApp.swift         # point d'entrée
├── Navigation/AppShellView.swift  # TabView (5 onglets) + toolbar sauge + feuille "Navigation"
├── Theme/AppTheme.swift       # palette (sauge / orange / crème)
├── Models/Models.swift        # entités + enums (Site, SiteTask, MediaItem, SiteAnnotation, DroneSession, User)
├── Data/                      # dépôts mock en mémoire (ObservableObject, @Published)
│                              #   + locator `Repositories` (point d'injection pour la future API)
├── Components/                # StatCard, StatusBadge, PriorityBadge, EmptyState, BrandLogo, visuels
├── Screens/                   # 9 écrans (Dashboard, Sites, SiteDetail, Tasks, TaskDetail,
│                              #   Drone, MediaGallery, Annotations, Profile)
└── Utils/Formatters.swift     # dates dd/MM/yyyy
```

## Limitations connues (alpha)

- Données volatiles : toute modification est perdue au redémarrage de l'app.
- "Capturer" (drone) et "Déconnexion" sont des stubs volontaires.
- Les visuels sont des placeholders dégradés + SF Symbols (pas d'assets réseau).
- **Non compilé à ce stade** : développé sans Xcode (machine Windows) —
  une passe de compilation sur Mac peut révéler des ajustements mineurs.
