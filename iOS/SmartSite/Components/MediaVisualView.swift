import SwiftUI

/// Renders a media item's visual: the user's picked image data if present,
/// otherwise the bundled gradient placeholder.
struct MediaVisualView: View {
    let item: MediaItem

    var body: some View {
        if let data = item.imageData, let uiImage = UIImage(data: data) {
            Image(uiImage: uiImage)
                .resizable()
                .scaledToFill()
        } else if let visual = item.visual {
            ZStack {
                LinearGradient(
                    colors: visual.colors,
                    startPoint: .topLeading,
                    endPoint: .bottomTrailing
                )
                Image(systemName: visual.symbol)
                    .font(.system(size: 28))
                    .foregroundStyle(.white.opacity(0.6))
            }
        } else {
            Color(hex: "#E5E7EB")
        }
    }
}

/// Gradient cover for a site (or a neutral placeholder when none is set).
struct SiteCoverView: View {
    let cover: MockVisual?

    var body: some View {
        ZStack {
            LinearGradient(
                colors: cover?.colors ?? [AppTheme.sage, AppTheme.sageDark],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            Image(systemName: cover?.symbol ?? "building.2.fill")
                .font(.system(size: 32))
                .foregroundStyle(.white.opacity(0.6))
        }
    }
}
