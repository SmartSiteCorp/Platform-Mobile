import Foundation

/// In-memory replacement for the Base44 SDK `auth.me()` call.
/// The alpha uses a single fixed user (admin role).
final class MockAuthRepository: ObservableObject {
    static let shared = MockAuthRepository()

    @Published private(set) var currentUser = User(
        fullName: "Marie Dupont",
        email: "marie.dupont@smartsite.fr",
        role: .admin,
        phone: "06 12 34 56 78",
        specialty: "Conductrice de travaux"
    )

    func update(_ user: User) {
        currentUser = user
    }
}

/// Access point mirroring Android's `Repositories` locator — the future
/// API-backed implementations will be swapped in here.
enum Repositories {
    static let sites = MockSiteRepository.shared
    static let tasks = MockTaskRepository.shared
    static let media = MockMediaRepository.shared
    static let annotations = MockAnnotationRepository.shared
    static let drone = MockDroneRepository.shared
    static let auth = MockAuthRepository.shared
}
