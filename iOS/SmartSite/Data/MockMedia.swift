import Foundation
import SwiftUI

/// In-memory replacement for the Base44 SDK `Media` entity.
final class MockMediaRepository: ObservableObject {
    static let shared = MockMediaRepository()

    @Published private(set) var media: [MediaItem] = [
        MediaItem(
            id: "media-dalle",
            visual: MockVisual(colors: [Color(hex: "#B8B2A6"), Color(hex: "#6E675C")], symbol: "camera.fill"),
            type: .photo,
            caption: "Coulage de la dalle — Bâtiment A",
            siteId: "site-jardins",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 24))
        ),
        MediaItem(
            id: "media-facade",
            visual: MockVisual(colors: [Color(hex: "#9FC4E8"), Color(hex: "#4A6E99")], symbol: "camera.fill"),
            type: .droneCapture,
            caption: "Vue drone façade ouest — Tour Horizon",
            siteId: "site-horizon",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 30))
        ),
        MediaItem(
            id: "media-terrain",
            visual: MockVisual(colors: [Color(hex: "#A8C89E"), Color(hex: "#54714B")], symbol: "camera.fill"),
            type: .droneCapture,
            caption: "Survol terrain — parcelle B",
            siteId: "site-olympe",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 18))
        ),
        MediaItem(
            id: "media-lidar",
            visual: MockVisual(colors: [Color(hex: "#1F2A33"), Color(hex: "#3E7A58")], symbol: "waveform.path.ecg"),
            type: .lidarScan,
            caption: "Relevé LiDAR charpente — Îlot Saint-Michel",
            siteId: "site-saint-michel",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 8, day: 12))
        ),
        MediaItem(
            id: "media-plan-ecole",
            visual: MockVisual(colors: [Color(hex: "#FAF8F5"), Color(hex: "#E2DDD6")], symbol: "doc.text.image"),
            type: .plan,
            caption: "Plan R+1 — École Jean Moulin",
            siteId: "site-ecole",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 1, day: 20))
        ),
        MediaItem(
            id: "media-plan-jardins",
            visual: MockVisual(colors: [Color(hex: "#FAF8F5"), Color(hex: "#D8D2C8")], symbol: "doc.text.image"),
            type: .plan,
            caption: "Plan RDC — Résidence Les Jardins",
            siteId: "site-jardins",
            createdDate: Formatters.date(DateComponents(year: 2025, month: 8, day: 22))
        ),
        MediaItem(
            id: "media-halle",
            visual: MockVisual(colors: [Color(hex: "#86C8A2"), Color(hex: "#3E7A58")], symbol: "camera.fill"),
            type: .photo,
            caption: "Halle livrée — vue extérieure",
            siteId: "site-verdun",
            createdDate: Formatters.date(DateComponents(year: 2026, month: 4, day: 28))
        )
    ]

    @discardableResult
    func create(imageData: Data?, type: MediaType, caption: String, siteId: String) -> MediaItem {
        let item = MediaItem(
            id: "media-\(UUID().uuidString)",
            imageData: imageData,
            visual: nil,
            type: type,
            caption: caption,
            siteId: siteId,
            createdDate: Date()
        )
        media.append(item)
        return item
    }
}
