import Foundation

/// In-memory replacement for the Base44 SDK `DroneSession` entity.
/// Holds at most one session, mirroring the mockup's single live feed.
final class MockDroneRepository: ObservableObject {
    static let shared = MockDroneRepository()

    @Published private(set) var session: DroneSession?

    @discardableResult
    func createSession(operatorEmail: String) -> DroneSession {
        let session = DroneSession(
            status: .idle,
            batteryLevel: 100,
            altitude: 0,
            gpsLat: 48.8566,
            gpsLng: 2.3522,
            missionName: "Nouvelle mission",
            streamShared: false,
            operatorEmail: operatorEmail
        )
        self.session = session
        return session
    }

    func update(_ transform: (inout DroneSession) -> Void) {
        guard var current = session else { return }
        transform(&current)
        session = current
    }
}
