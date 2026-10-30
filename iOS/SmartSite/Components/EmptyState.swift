import SwiftUI

/// Centered empty state: 64pt gray rounded icon tile + bold title + muted description.
struct EmptyState: View {
    let icon: String // SF Symbol
    let title: String
    let description: String

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: icon)
                .font(.system(size: 26))
                .foregroundStyle(AppTheme.textMuted)
                .frame(width: 64, height: 64)
                .background(Color(hex: "#E5E7EB"))
                .clipShape(RoundedRectangle(cornerRadius: 16))
            VStack(spacing: 6) {
                Text(title)
                    .font(.system(size: 16, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text(description)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textMuted)
                    .multilineTextAlignment(.center)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(32)
    }
}

#Preview {
    EmptyState(icon: "building.2", title: "Aucun chantier trouvé", description: "Créez votre premier chantier pour commencer")
}
