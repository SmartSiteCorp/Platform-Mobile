import SwiftUI

/// Detail destinations shared by every tab's NavigationStack.
enum AppRoute: Hashable {
    case siteDetail(String)
    case taskDetail(String)
}

/// Lets any screen switch the selected bottom tab (e.g. "Tout voir →").
private struct OpenTabKey: EnvironmentKey {
    static let defaultValue: (Int) -> Void = { _ in }
}

extension EnvironmentValues {
    var openTab: (Int) -> Void {
        get { self[OpenTabKey.self] }
        set { self[OpenTabKey.self] = newValue }
    }
}

/// Bottom tab indices (admin set — mirrors the Android NavConfig).
enum AppTab {
    static let dashboard = 0
    static let sites = 1
    static let tasks = 2
    static let media = 3
    static let drone = 4
}

struct AppShellView: View {
    @StateObject private var auth = Repositories.auth
    @State private var selectedTab = AppTab.dashboard
    @State private var showNavSheet = false
    @State private var showProfile = false
    @State private var showAnnotations = false

    var body: some View {
        TabView(selection: $selectedTab) {
            shellTab(DashboardView(), label: "Tableau de bord", icon: "house.fill", tag: AppTab.dashboard)
            shellTab(SitesView(), label: "Chantiers", icon: "map.fill", tag: AppTab.sites)
            shellTab(TasksView(), label: "Tâches", icon: "list.clipboard.fill", tag: AppTab.tasks)
            shellTab(MediaGalleryView(), label: "Médias", icon: "photo.fill", tag: AppTab.media)
            shellTab(DroneView(), label: "Drone", icon: "antenna.radiowaves.left.and.right", tag: AppTab.drone)
        }
        .tint(AppTheme.orange)
        .environment(\.openTab) { tab in selectedTab = tab }
        .sheet(isPresented: $showNavSheet) { navSheet }
        .sheet(isPresented: $showProfile) {
            NavigationStack { ProfileView() }
        }
        .sheet(isPresented: $showAnnotations) {
            NavigationStack { AnnotationsView() }
        }
    }

    private func shellTab<Content: View>(_ content: Content, label: String, icon: String, tag: Int) -> some View {
        NavigationStack {
            content
                .navigationDestination(for: AppRoute.self) { route in
                    switch route {
                    case .siteDetail(let id): SiteDetailView(siteId: id)
                    case .taskDetail(let id): TaskDetailView(taskId: id)
                    }
                }
                .toolbar {
                    ToolbarItem(placement: .topBarLeading) {
                        Button {
                            showNavSheet = true
                        } label: {
                            Image(systemName: "line.3.horizontal")
                                .foregroundStyle(.white)
                        }
                    }
                    ToolbarItem(placement: .principal) {
                        BrandLogo(tileSize: 26)
                    }
                    ToolbarItem(placement: .topBarTrailing) {
                        Menu {
                            VStack(alignment: .leading, spacing: 2) {
                                Text(auth.currentUser.fullName).font(.headline)
                                Text(auth.currentUser.role.frenchLabel)
                                    .font(.caption)
                                    .foregroundStyle(AppTheme.textMuted)
                            }
                            Divider()
                            Button {
                                showProfile = true
                            } label: {
                                Label("Profil", systemImage: "person.fill")
                            }
                            Button(role: .destructive) {
                                // Stub in the alpha
                            } label: {
                                Label("Déconnexion", systemImage: "rectangle.portrait.and.arrow.right")
                            }
                        } label: {
                            Text(auth.currentUser.fullName.prefix(1).uppercased())
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(.white)
                                .frame(width: 32, height: 32)
                                .background(AppTheme.sageDark)
                                .clipShape(Circle())
                        }
                    }
                }
                .toolbarBackground(AppTheme.sage, for: .navigationBar)
                .toolbarBackground(.visible, for: .navigationBar)
        }
        .tabItem { Label(label, systemImage: icon) }
        .tag(tag)
    }

    /// Drawer equivalent: dark sage sheet with every destination of the role.
    private var navSheet: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 4) {
                BrandLogo(tileSize: 34)
                    .padding(.bottom, 20)
                Text("Navigation".uppercased())
                    .font(.system(size: 11, weight: .medium))
                    .foregroundStyle(.white.opacity(0.7))
                    .padding(.leading, 12)
                    .padding(.bottom, 8)
                navItem("Tableau de bord", icon: "house.fill", tab: AppTab.dashboard)
                navItem("Chantiers", icon: "map.fill", tab: AppTab.sites)
                navItem("Tâches", icon: "list.clipboard.fill", tab: AppTab.tasks)
                navItem("Médias", icon: "photo.fill", tab: AppTab.media)
                navItem("Annotations", icon: "pencil.tip", tab: nil)
                navItem("Drone", icon: "antenna.radiowaves.left.and.right", tab: AppTab.drone)
                Spacer()
            }
            .padding(16)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
            .background(AppTheme.sageDark.ignoresSafeArea())
        }
        .presentationDetents([.medium, .large])
    }

    private func navItem(_ label: String, icon: String, tab: Int?) -> some View {
        Button {
            showNavSheet = false
            if let tab {
                selectedTab = tab
            } else {
                // Annotations has no tab of its own — presented as a sheet.
                showAnnotations = true
            }
        } label: {
            Label(label, systemImage: icon)
                .font(.system(size: 16, weight: .medium))
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.horizontal, 12)
                .padding(.vertical, 12)
        }
    }
}

#Preview {
    AppShellView()
}
