import Foundation

/// In-memory replacement for the Base44 SDK `Task` entity.
final class MockTaskRepository: ObservableObject {
    static let shared = MockTaskRepository()

    @Published private(set) var tasks: [SiteTask] = [
        SiteTask(
            id: "task-dalle",
            title: "Coulage dalle RDC",
            description: "Coulage de la dalle béton du rez-de-chaussée, bâtiment A. Prévoir 42 m³ de béton C25/30.",
            status: .inProgress,
            priority: .high,
            siteId: "site-jardins",
            zone: "Bâtiment A",
            assignedTo: "pierre.martin@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 9))
        ),
        SiteTask(
            id: "task-menuiseries",
            title: "Pose menuiseries extérieures",
            description: "Pose des menuiseries aluminium du bâtiment B (34 fenêtres, 6 baies vitrées).",
            status: .pending,
            priority: .medium,
            siteId: "site-jardins",
            zone: "Bâtiment B",
            assignedTo: "marie.dupont@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 14))
        ),
        SiteTask(
            id: "task-vmc",
            title: "Vérification réseau VMC",
            description: "Contrôle d'étanchéité du réseau VMC double flux au niveau R+3 avant fermeture des plafonds.",
            status: .pending,
            priority: .urgent,
            siteId: "site-horizon",
            zone: "Niveau R+3",
            assignedTo: "marie.dupont@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 6))
        ),
        SiteTask(
            id: "task-etancheite",
            title: "Étanchéité toiture",
            description: "Pose de la membrane EPDM sur la toiture terrasse, zone sud.",
            status: .blocked,
            priority: .high,
            siteId: "site-horizon",
            zone: "Toiture",
            assignedTo: "lucas.bernard@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 8)),
            blockingReason: "Intempéries : vents violents annoncés toute la semaine, reprise impossible en sécurité."
        ),
        SiteTask(
            id: "task-peinture",
            title: "Finitions peinture",
            description: "Deux couches de peinture acrylique dans la salle principale et le vestiaire.",
            status: .completed,
            priority: .low,
            siteId: "site-verdun",
            zone: "Salle principale",
            assignedTo: "sophie.leroy@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 4, day: 18)),
            completionComment: "Travaux réceptionnés avec le conducteur d'opération, aucune réserve."
        ),
        SiteTask(
            id: "task-terrassement",
            title: "Terrassement parcelle B",
            description: "Décapage et mise à niveau de la parcelle B pour les cellules 5 à 8.",
            status: .pending,
            priority: .medium,
            siteId: "site-olympe",
            zone: "Parcelle B",
            assignedTo: "pierre.martin@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 11, day: 20))
        ),
        SiteTask(
            id: "task-beton",
            title: "Contrôle béton armé",
            description: "Contrôle visuel et sondes du voile béton armé du bâtiment C après décoffrage.",
            status: .inProgress,
            priority: .high,
            siteId: "site-ecole",
            zone: "Bâtiment C",
            assignedTo: "marie.dupont@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 10, day: 10))
        ),
        SiteTask(
            id: "task-plomberie",
            title: "Réception lot plomberie",
            description: "Réception du lot plomberie-sanitaire de la résidence, épreuves d'eau incluses.",
            status: .completed,
            priority: .medium,
            siteId: "site-jardins",
            zone: "Bâtiment A",
            assignedTo: "sophie.leroy@smartsite.fr",
            dueDate: Formatters.date(DateComponents(year: 2026, month: 9, day: 28)),
            completionComment: "Épreuves d'eau conformes, PV signé."
        )
    ]

    func task(byId id: String) -> SiteTask? {
        tasks.first { $0.id == id }
    }

    @discardableResult
    func create(title: String, siteId: String, description: String, priority: TaskPriority, zone: String, assignedTo: String, dueDate: Date) -> SiteTask {
        let task = SiteTask(
            id: "task-\(UUID().uuidString)",
            title: title,
            description: description,
            status: .pending,
            priority: priority,
            siteId: siteId,
            zone: zone,
            assignedTo: assignedTo,
            dueDate: dueDate
        )
        tasks.append(task)
        return task
    }

    /// Replaces the task with the same id.
    func update(_ task: SiteTask) {
        guard let index = tasks.firstIndex(where: { $0.id == task.id }) else { return }
        tasks[index] = task
    }
}
