import SwiftUI

/// Site detail: header card, task stats, then Tâches / Médias / Plans tabs.
/// Pushed via `AppRoute.siteDetail`; back navigation uses the system back button.
struct SiteDetailView: View {
    let siteId: String

    @StateObject private var siteRepo = Repositories.sites
    @StateObject private var taskRepo = Repositories.tasks
    @StateObject private var mediaRepo = Repositories.media
    @State private var selectedTab: DetailTab = .tasks

    private enum DetailTab: Hashable {
        case tasks
        case media
        case plans
    }

    private let twoColumns = [
        GridItem(.flexible(), spacing: 12),
        GridItem(.flexible(), spacing: 12)
    ]

    private var site: Site? { siteRepo.site(byId: siteId) }
    private var siteTasks: [SiteTask] { taskRepo.tasks.filter { $0.siteId == siteId } }
    private var siteMedia: [MediaItem] { mediaRepo.media.filter { $0.siteId == siteId } }

    var body: some View {
        Group {
            if let site {
                detailContent(site)
            } else {
                Text("Chantier introuvable")
                    .font(.system(size: 16, weight: .medium))
                    .foregroundStyle(AppTheme.textMuted)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            }
        }
        .background(AppTheme.cream)
        .navigationTitle(site?.name ?? "Chantier")
        .navigationBarTitleDisplayMode(.inline)
    }

    private func detailContent(_ site: Site) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                headerCard(site)
                statsGrid
                tabPicker
                tabContent(site)
            }
            .padding(16)
        }
        .background(AppTheme.cream)
    }

    // MARK: - Header

    private func headerCard(_ site: Site) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(alignment: .top, spacing: 12) {
                Image(systemName: "building.2.fill")
                    .font(.system(size: 22))
                    .foregroundStyle(.white)
                    .frame(width: 52, height: 52)
                    .background(AppTheme.accentBlue)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                VStack(alignment: .leading, spacing: 6) {
                    Text(site.name)
                        .font(.system(size: 20, weight: .bold))
                        .foregroundStyle(AppTheme.textDark)
                    StatusBadge(status: site.status)
                }
            }
            infoRow(icon: "mappin", text: site.address)
            infoRow(icon: "calendar", text: "\(site.startDate.frenchFormatted) — \(site.endDate.frenchFormatted)")
            if !site.description.isEmpty {
                Text(site.description)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textMuted)
                    .fixedSize(horizontal: false, vertical: true)
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

    private func infoRow(icon: String, text: String) -> some View {
        HStack(spacing: 8) {
            Image(systemName: icon)
            Text(text)
        }
        .font(.system(size: 14))
        .foregroundStyle(AppTheme.textMuted)
    }

    // MARK: - Stats

    private var statsGrid: some View {
        let total = siteTasks.count
        let inProgress = siteTasks.filter { $0.status == .inProgress }.count
        let completed = siteTasks.filter { $0.status == .completed }.count
        let blocked = siteTasks.filter { $0.status == .blocked }.count
        return LazyVGrid(columns: twoColumns, spacing: 12) {
            StatCard(title: "Total tâches", value: "\(total)", icon: "list.clipboard.fill", color: AppTheme.accentPurple)
            StatCard(title: "En cours", value: "\(inProgress)", icon: "clock.fill", color: AppTheme.accentBlue)
            StatCard(title: "Terminées", value: "\(completed)", icon: "checkmark.circle.fill", color: AppTheme.accentGreen)
            StatCard(title: "Bloquées", value: "\(blocked)", icon: "exclamationmark.triangle.fill", color: AppTheme.accentRed)
        }
    }

    // MARK: - Tabs

    private var tabPicker: some View {
        Picker("Section", selection: $selectedTab) {
            Text("Tâches (\(siteTasks.count))").tag(DetailTab.tasks)
            Text("Médias (\(siteMedia.count))").tag(DetailTab.media)
            Text("Plans").tag(DetailTab.plans)
        }
        .pickerStyle(.segmented)
    }

    @ViewBuilder
    private func tabContent(_ site: Site) -> some View {
        switch selectedTab {
        case .tasks: tasksTab
        case .media: mediaTab
        case .plans: plansTab(site)
        }
    }

    private var tasksTab: some View {
        VStack(spacing: 12) {
            if siteTasks.isEmpty {
                EmptyState(
                    icon: "list.clipboard",
                    title: "Aucune tâche",
                    description: "Aucune tâche n'est encore associée à ce chantier"
                )
            } else {
                ForEach(siteTasks) { task in
                    NavigationLink(value: AppRoute.taskDetail(task.id)) {
                        taskCard(task)
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }

    private func taskCard(_ task: SiteTask) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(task.title)
                .font(.system(size: 15, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            HStack(spacing: 8) {
                StatusBadge(status: task.status)
                PriorityBadge(priority: task.priority)
            }
            Text("Zone : \(task.zone)")
                .font(.system(size: 13))
                .foregroundStyle(AppTheme.textMuted)
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }

    private var mediaTab: some View {
        VStack(spacing: 12) {
            if siteMedia.isEmpty {
                EmptyState(
                    icon: "photo",
                    title: "Aucun média",
                    description: "Aucun média n'est encore associé à ce chantier"
                )
            } else {
                LazyVGrid(columns: twoColumns, spacing: 12) {
                    ForEach(siteMedia) { item in
                        mediaTile(item)
                    }
                }
            }
        }
    }

    private func mediaTile(_ item: MediaItem) -> some View {
        ZStack(alignment: .bottomLeading) {
            Rectangle()
                .fill(.clear)
                .aspectRatio(1, contentMode: .fit)
                .overlay(MediaVisualView(item: item))
            LinearGradient(
                colors: [.clear, .black.opacity(0.75)],
                startPoint: .center,
                endPoint: .bottom
            )
            VStack(alignment: .leading, spacing: 2) {
                Text(item.caption)
                    .font(.system(size: 12, weight: .semibold))
                    .foregroundStyle(.white)
                    .lineLimit(2)
                Text(item.type.frenchLabel)
                    .font(.system(size: 10, weight: .medium))
                    .foregroundStyle(.white.opacity(0.8))
            }
            .padding(10)
        }
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func plansTab(_ site: Site) -> some View {
        VStack(spacing: 12) {
            if site.planCount <= 0 {
                EmptyState(
                    icon: "doc.text.image",
                    title: "Aucun plan disponible",
                    description: "Les plans de ce chantier apparaîtront ici"
                )
            } else {
                LazyVGrid(columns: twoColumns, spacing: 12) {
                    ForEach(1...site.planCount, id: \.self) { index in
                        planTile(index)
                    }
                }
            }
        }
    }

    private func planTile(_ index: Int) -> some View {
        VStack(spacing: 8) {
            Image(systemName: "doc.text.image")
                .font(.system(size: 28))
                .foregroundStyle(AppTheme.textMuted)
            Text("Plan \(index)")
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(AppTheme.textDark)
        }
        .frame(maxWidth: .infinity)
        .frame(height: 120)
        .background(AppTheme.cream)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }
}

private extension MediaType {
    var frenchLabel: String {
        switch self {
        case .photo: return "Photo"
        case .droneCapture: return "Drone"
        case .lidarScan: return "LiDAR"
        case .plan: return "Plan"
        case .document: return "Document"
        }
    }
}

#Preview {
    NavigationStack {
        SiteDetailView(siteId: "site-jardins")
    }
}
