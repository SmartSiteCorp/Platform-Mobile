import Foundation
import SwiftUI

/// In-memory replacement for the Base44 SDK `Site` entity.
/// All mutations update `@Published` immediately; nothing is persisted.
final class MockSiteRepository: ObservableObject {
    static let shared = MockSiteRepository()

    @Published private(set) var sites: [Site] = [
        Site(
            id: "site-jardins",
            name: "Résidence Les Jardins",
            address: "12 rue des Lilas, 69003 Lyon",
            description: "Construction d'une résidence de 24 logements collectifs avec parking souterrain et espaces verts.",
            status: .inProgress,
            startDate: Formatters.date(DateComponents(year: 2025, month: 9, day: 1)),
            endDate: Formatters.date(DateComponents(year: 2026, month: 6, day: 30)),
            cover: .site([Color(hex: "#8FA99B"), Color(hex: "#5A7A6B")]),
            planCount: 3
        ),
        Site(
            id: "site-horizon",
            name: "Tour Horizon",
            address: "8 quai du Commerce, 44000 Nantes",
            description: "Tour de bureaux de 12 étages, certification HQE visée.",
            status: .inProgress,
            startDate: Formatters.date(DateComponents(year: 2025, month: 6, day: 15)),
            endDate: Formatters.date(DateComponents(year: 2027, month: 2, day: 28)),
            cover: .site([Color(hex: "#7FA8D8"), Color(hex: "#3D5F8A")]),
            planCount: 2
        ),
        Site(
            id: "site-olympe",
            name: "Parc d'activités Olympe",
            address: "ZA des Oliviers, 13011 Marseille",
            description: "Aménagement d'un parc d'activités de 8 cellules industrielles.",
            status: .planning,
            startDate: Formatters.date(DateComponents(year: 2026, month: 11, day: 1)),
            endDate: Formatters.date(DateComponents(year: 2027, month: 9, day: 30)),
            cover: .site([Color(hex: "#E0B06A"), Color(hex: "#9C7A33")]),
            planCount: 1
        ),
        Site(
            id: "site-saint-michel",
            name: "Îlot Saint-Michel",
            address: "3 place Saint-Michel, 33000 Bordeaux",
            description: "Rénovation lourde d'un îlot ancien : reprise en sous-œuvre et surélévation.",
            status: .onHold,
            startDate: Formatters.date(DateComponents(year: 2025, month: 3, day: 1)),
            endDate: Formatters.date(DateComponents(year: 2026, month: 12, day: 15)),
            cover: .site([Color(hex: "#D89AAC"), Color(hex: "#9E5A70")]),
            planCount: 2
        ),
        Site(
            id: "site-verdun",
            name: "Halle des sports de Verdun",
            address: "25 avenue Foch, 59120 Loos",
            description: "Construction d'une halle sportive de 2 400 m² avec tribunes.",
            status: .completed,
            startDate: Formatters.date(DateComponents(year: 2024, month: 11, day: 4)),
            endDate: Formatters.date(DateComponents(year: 2026, month: 4, day: 30)),
            cover: .site([Color(hex: "#86C8A2"), Color(hex: "#3E7A58")]),
            planCount: 2
        ),
        Site(
            id: "site-ecole",
            name: "École primaire Jean Moulin",
            address: "17 rue de la République, 31000 Toulouse",
            description: "Extension de l'école : 6 classes, une cantine et une cour végétalisée.",
            status: .inProgress,
            startDate: Formatters.date(DateComponents(year: 2026, month: 1, day: 12)),
            endDate: Formatters.date(DateComponents(year: 2026, month: 12, day: 20)),
            cover: .site([Color(hex: "#A99BCB"), Color(hex: "#6A5896")]),
            planCount: 1
        )
    ]

    func site(byId id: String) -> Site? {
        sites.first { $0.id == id }
    }

    @discardableResult
    func create(name: String, address: String, description: String, startDate: Date, endDate: Date) -> Site {
        let site = Site(
            id: "site-\(UUID().uuidString)",
            name: name,
            address: address,
            description: description,
            status: .planning,
            startDate: startDate,
            endDate: endDate,
            cover: nil,
            planCount: 0
        )
        sites.append(site)
        return site
    }
}
