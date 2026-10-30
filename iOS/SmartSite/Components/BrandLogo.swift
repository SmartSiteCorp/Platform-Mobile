import SwiftUI

/// SmartSite branding: orange rounded square with white "SS" + "SmartSite"
/// wordmark ("Site" tinted #FFD4AB).
struct BrandLogo: View {
    var tileSize: CGFloat = 30
    var showWordmark: Bool = true
    var wordmarkColor: Color = .white

    var body: some View {
        HStack(spacing: 8) {
            Text("SS")
                .font(.system(size: tileSize * 0.42, weight: .bold))
                .foregroundStyle(.white)
                .frame(width: tileSize, height: tileSize)
                .background(AppTheme.orange)
                .clipShape(RoundedRectangle(cornerRadius: 8))
            if showWordmark {
                (Text("Smart").foregroundStyle(wordmarkColor)
                    + Text("Site").foregroundStyle(AppTheme.orangeTint))
                    .font(.system(size: 17, weight: .bold))
            }
        }
    }
}

#Preview {
    BrandLogo()
        .padding()
        .background(AppTheme.sage)
}
