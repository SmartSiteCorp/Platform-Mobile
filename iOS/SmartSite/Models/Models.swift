import Foundation
import SwiftUI

// MARK: - Visual placeholders
// Bundled mock "images": gradient + SF Symbol (no network URLs, no binary assets).

struct MockVisual: Equatable, Hashable {
    let colors: [Color]
    let symbol: String

    static func site(_ colors: [Color]) -> MockVisual {
        MockVisual(colors: colors, symbol: "building.2.fill")
    }
}

// MARK: - Site

enum SiteStatus: String, CaseIterable, Hashable {
    case planning
    case inProgress = "in_progress"
    case completed
    case onHold = "on_hold"
}

struct Site: Identifiable, Hashable {
    let id: String
    var name: String
    var address: String
    var description: String
    var status: SiteStatus
    var startDate: Date
    var endDate: Date
    var cover: MockVisual?
    var planCount: Int
}

// MARK: - Task

enum TaskStatus: String, CaseIterable, Hashable {
    case pending
    case inProgress = "in_progress"
    case completed
    case blocked
}

enum TaskPriority: String, CaseIterable, Hashable {
    case low
    case medium
    case high
    case urgent
}

struct SiteTask: Identifiable, Hashable {
    let id: String
    var title: String
    var description: String
    var status: TaskStatus
    var priority: TaskPriority
    var siteId: String
    var zone: String
    var assignedTo: String // assignee email
    var dueDate: Date
    var completionComment: String?
    var completionPhoto: Data?
    var blockingReason: String?
    var blockingPhoto: Data?
}

// MARK: - Media

enum MediaType: String, CaseIterable, Hashable {
    case photo
    case droneCapture = "drone_capture"
    case lidarScan = "lidar_scan"
    case plan
    case document
}

struct MediaItem: Identifiable, Hashable {
    let id: String
    var imageData: Data?      // user uploads (PhotosPicker / camera)
    var visual: MockVisual?   // bundled mock placeholder
    var type: MediaType
    var caption: String
    var siteId: String
    var createdDate: Date
}

// MARK: - Annotation

enum AnnotationType: String, CaseIterable, Hashable {
    case text
    case arrow
    case circle
    case freehand
}

enum AnnotationSupport: String, CaseIterable, Hashable {
    case plan
    case image
    case droneCapture = "drone_capture"
}

struct SiteAnnotation: Identifiable, Hashable {
    let id: String
    var type: AnnotationType
    var content: String
    var support: AnnotationSupport
    var zone: String
    var colorHex: String // e.g. "#EF4444"
    var siteId: String
    var authorRole: UserRole
    var createdDate: Date

    var color: Color { Color(hex: colorHex) }
}

// MARK: - Drone

enum DroneStatus: String, CaseIterable, Hashable {
    case idle
    case flying
    case paused
    case returning
    case landed
}

struct DroneSession: Equatable {
    var status: DroneStatus
    var batteryLevel: Int // 0..100
    var altitude: Float   // meters
    var gpsLat: Double
    var gpsLng: Double
    var missionName: String
    var streamShared: Bool
    var operatorEmail: String
}

// MARK: - User

enum UserRole: String, CaseIterable, Hashable {
    case admin
    case architect
    case worker
    case projectManager = "project_manager"
    case droneOperator = "drone_operator"

    var frenchLabel: String {
        switch self {
        case .admin: return "Administrateur"
        case .architect: return "Architecte"
        case .worker: return "Ouvrier"
        case .projectManager: return "Chef de projet"
        case .droneOperator: return "Droniste"
        }
    }
}

struct User: Equatable {
    var fullName: String
    var email: String
    var role: UserRole
    var phone: String
    var specialty: String
}
