import SwiftUI

/// Small outlined pill with a colored French label.
struct BadgePill: View {
    let text: String
    let color: Color

    var body: some View {
        Text(text)
            .font(.system(size: 11, weight: .semibold))
            .foregroundStyle(color)
            .padding(.horizontal, 10)
            .padding(.vertical, 3)
            .overlay(
                Capsule().stroke(color.opacity(0.5), lineWidth: 1)
            )
    }
}

struct StatusBadge: View {
    let text: String
    let color: Color

    init(text: String, color: Color) {
        self.text = text
        self.color = color
    }

    init(status: TaskStatus) {
        switch status {
        case .pending: self.init(text: "En attente", color: AppTheme.slate)
        case .inProgress: self.init(text: "En cours", color: AppTheme.accentBlue)
        case .completed: self.init(text: "Terminé", color: AppTheme.accentGreen)
        case .blocked: self.init(text: "Bloqué", color: AppTheme.accentRed)
        }
    }

    init(status: SiteStatus) {
        switch status {
        case .planning: self.init(text: "Planification", color: AppTheme.accentPurple)
        case .inProgress: self.init(text: "En cours", color: AppTheme.accentBlue)
        case .completed: self.init(text: "Terminé", color: AppTheme.accentGreen)
        case .onHold: self.init(text: "En pause", color: AppTheme.accentAmber)
        }
    }

    init(status: DroneStatus) {
        switch status {
        case .idle: self.init(text: "Inactif", color: AppTheme.slate)
        case .flying: self.init(text: "En vol", color: AppTheme.accentBlue)
        case .paused: self.init(text: "En pause", color: AppTheme.accentAmber)
        case .returning: self.init(text: "Retour", color: AppTheme.accentOrange)
        case .landed: self.init(text: "Posé", color: AppTheme.accentGreen)
        }
    }

    var body: some View {
        BadgePill(text: text, color: color)
    }
}

struct PriorityBadge: View {
    let text: String
    let color: Color

    init(priority: TaskPriority) {
        switch priority {
        case .low:
            text = "Faible"; color = AppTheme.slate
        case .medium:
            text = "Moyen"; color = AppTheme.accentBlue
        case .high:
            text = "Élevé"; color = AppTheme.accentOrange
        case .urgent:
            text = "Urgent"; color = AppTheme.accentRed
        }
    }

    var body: some View {
        BadgePill(text: text, color: color)
    }
}

#Preview {
    HStack(spacing: 6) {
        StatusBadge(status: TaskStatus.inProgress)
        PriorityBadge(priority: .urgent)
        StatusBadge(status: DroneStatus.returning)
    }
    .padding()
}
