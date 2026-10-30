import SwiftUI

extension Color {
    init(hex: String) {
        var sanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        sanitized = sanitized.replacingOccurrences(of: "#", with: "")
        var rgb: UInt64 = 0
        Scanner(string: sanitized).scanHexInt64(&rgb)
        self.init(
            red: Double((rgb >> 16) & 0xFF) / 255.0,
            green: Double((rgb >> 8) & 0xFF) / 255.0,
            blue: Double(rgb & 0xFF) / 255.0
        )
    }
}

/// SmartSite palette (from the web mockup / Android alpha).
enum AppTheme {
    static let sage = Color(hex: "#6B8F7E")        // top bar
    static let sageDark = Color(hex: "#5A7A6B")    // drawer
    static let orange = Color(hex: "#E07A3A")      // primary buttons / active nav
    static let orangeTint = Color(hex: "#FFD4AB")  // "Site" wordmark tint
    static let cream = Color(hex: "#F5F3EF")       // app background
    static let cardWhite = Color.white
    static let textDark = Color(hex: "#2D3748")
    static let textMuted = Color(hex: "#718096")
    static let borderLight = Color(hex: "#E2DDD6")
    static let navInactive = Color(hex: "#9CA3AF")

    // StatCard accent palette
    static let accentSage = Color(hex: "#6B8F7E")
    static let accentOrange = Color(hex: "#E07A3A")
    static let accentBlue = Color(hex: "#5B89C8")
    static let accentGreen = Color(hex: "#56A87A")
    static let accentAmber = Color(hex: "#D4A03A")
    static let accentRed = Color(hex: "#D9534F")
    static let accentPurple = Color(hex: "#7C6DB5")
    static let accentRose = Color(hex: "#C96585")

    static let slate = Color(hex: "#64748B")
}
