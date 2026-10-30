import Foundation

/// In-memory replacement for the Base44 SDK `Annotation` entity.
final class MockAnnotationRepository: ObservableObject {
    static let shared = MockAnnotationRepository()

    @Published private(set) var annotations: [SiteAnnotation] = [
        SiteAnnotation(
            id: "anno-poutre",
            type: .circle,
            content: "Vérifier l'alignement de la poutre P12 avec le plan de coffrage.",
            support: .plan,
            zone: "Bâtiment A",
            colorHex: "#EF4444",
            siteId: "site-jardins",
            authorRole: .architect,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 26))
        ),
        SiteAnnotation(
            id: "anno-acces",
            type: .arrow,
            content: "Accès engins à déplacer côté sud pendant la phase terrassement.",
            support: .plan,
            zone: "Parcelle B",
            colorHex: "#D4A03A",
            siteId: "site-olympe",
            authorRole: .projectManager,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 19))
        ),
        SiteAnnotation(
            id: "anno-fissure",
            type: .freehand,
            content: "Fissure superficielle constatée sur le voile — à surveiller au prochain contrôle.",
            support: .image,
            zone: "Bâtiment C",
            colorHex: "#5B89C8",
            siteId: "site-ecole",
            authorRole: .worker,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 2))
        ),
        SiteAnnotation(
            id: "anno-epdm",
            type: .text,
            content: "Zone EPDM à reprendre après la période de grand vent.",
            support: .droneCapture,
            zone: "Toiture",
            colorHex: "#EF4444",
            siteId: "site-horizon",
            authorRole: .droneOperator,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 1))
        ),
        SiteAnnotation(
            id: "anno-reseaux",
            type: .arrow,
            content: "Passage des réseaux enterrés confirmé sous le radier nord.",
            support: .plan,
            zone: "Niveau -1",
            colorHex: "#56A87A",
            siteId: "site-jardins",
            authorRole: .architect,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 12))
        ),
        SiteAnnotation(
            id: "anno-halle",
            type: .text,
            content: "Réserve levée sur l'alignement des pannes métalliques.",
            support: .image,
            zone: "Salle principale",
            colorHex: "#7C6DB5",
            siteId: "site-verdun",
            authorRole: .architect,
            createdDate: Formatters.date(DateComponents(year: 2026, month: 4, day: 15))
        )
    ]

    @discardableResult
    func create(type: AnnotationType, content: String, support: AnnotationSupport, zone: String, colorHex: String, siteId: String, authorRole: UserRole) -> SiteAnnotation {
        let annotation = SiteAnnotation(
            id: "anno-\(UUID().uuidString)",
            type: type,
            content: content,
            support: support,
            zone: zone,
            colorHex: colorHex,
            siteId: siteId,
            authorRole: authorRole,
            createdDate: Date()
        )
        annotations.append(annotation)
        return annotation
    }
}
