import SwiftUI

/// White bordered card with an uppercase muted title, a bold 2xl value and a
/// rounded solid-color icon tile. Mirrors `StatCard.jsx` / Android StatCard.
struct StatCard: View {
    let title: String
    let value: String
    let icon: String // SF Symbol
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Image(systemName: icon)
                .font(.system(size: 18, weight: .semibold))
                .foregroundStyle(.white)
                .frame(width: 40, height: 40)
                .background(color)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            VStack(alignment: .leading, spacing: 4) {
                Text(title.uppercased())
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(AppTheme.textMuted)
                Text(value)
                    .font(.system(size: 26, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }
}

#Preview {
    StatCard(title: "Chantiers actifs", value: "3", icon: "building.2.fill", color: AppTheme.accentSage)
        .padding()
        .background(AppTheme.cream)
}
