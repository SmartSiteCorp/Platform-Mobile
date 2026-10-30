import SwiftUI

/// Accueil : salutation, statistiques clés, tâches récentes et chantiers.
/// Miroir de l'écran Dashboard de l'alpha Android.
struct DashboardView: View {
    @StateObject private var siteRepo = Repositories.sites
    @StateObject private var taskRepo = Repositories.tasks
    @StateObject private var auth = Repositories.auth
    @Environment(\.openTab) var openTab

    private var firstName: String {
        auth.currentUser.fullName
            .split(separator: " ")
            .first
            .map(String.init) ?? auth.currentUser.fullName
    }

    private var recentTasks: [SiteTask] { Array(taskRepo.tasks.prefix(5)) }
    private var recentSites: [Site] { Array(siteRepo.sites.prefix(5)) }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                greeting
                statsGrid
                tasksSection
                sitesSection
            }
            .padding(16)
            .padding(.bottom, 24)
        }
        .background(AppTheme.cream)
    }

    // MARK: - Greeting

    private var greeting: some View {
        VStack(alignment: .leading, spacing: 4) {
            (Text("Bonjour ")
                + Text(firstName).foregroundStyle(AppTheme.orange)
                + Text(" 👋"))
                .font(.system(size: 24, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            Text("Vue d'ensemble de vos chantiers")
                .font(.system(size: 14))
                .foregroundStyle(AppTheme.textMuted)
        }
    }

    // MARK: - Stats

    private var statsGrid: some View {
        let activeSites = siteRepo.sites.filter { $0.status == .inProgress }.count
        let completedTasks = taskRepo.tasks.filter { $0.status == .completed }.count
        let blockedTasks = taskRepo.tasks.filter { $0.status == .blocked }.count
        return LazyVGrid(
            columns: [GridItem(.flexible(), spacing: 12), GridItem(.flexible(), spacing: 12)],
            spacing: 12
        ) {
            StatCard(title: "Chantiers actifs", value: "\(activeSites)", icon: "building.2.fill", color: AppTheme.accentSage)
            StatCard(title: "Tâches totales", value: "\(taskRepo.tasks.count)", icon: "list.clipboard.fill", color: AppTheme.accentOrange)
            StatCard(title: "Terminées", value: "\(completedTasks)", icon: "checkmark.circle.fill", color: AppTheme.accentGreen)
            StatCard(title: "Bloquées", value: "\(blockedTasks)", icon: "exclamationmark.triangle.fill", color: AppTheme.accentRed)
        }
    }

    // MARK: - Recent tasks

    private var tasksSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionHeader(title: "Tâches récentes") {
                openTab(AppTab.tasks)
            }
            if recentTasks.isEmpty {
                EmptyState(
                    icon: "list.clipboard",
                    title: "Aucune tâche pour le moment",
                    description: "Les nouvelles tâches apparaîtront ici dès leur création."
                )
                .background(cardBackground)
            } else {
                ForEach(recentTasks) { task in
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
                .frame(maxWidth: .infinity, alignment: .leading)
            HStack(spacing: 6) {
                StatusBadge(status: task.status)
                PriorityBadge(priority: task.priority)
            }
            HStack(spacing: 6) {
                Image(systemName: "clock")
                    .font(.system(size: 12))
                Text(task.dueDate.frenchFormatted)
                    .font(.system(size: 13))
            }
            .foregroundStyle(AppTheme.textMuted)
        }
        .padding(16)
        .background(cardBackground)
    }

    // MARK: - Sites

    private var sitesSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionHeader(title: "Chantiers") {
                openTab(AppTab.sites)
            }
            if recentSites.isEmpty {
                EmptyState(
                    icon: "building.2",
                    title: "Aucun chantier",
                    description: "Créez votre premier chantier pour commencer."
                )
                .background(cardBackground)
            } else {
                ForEach(recentSites) { site in
                    NavigationLink(value: AppRoute.siteDetail(site.id)) {
                        HStack {
                            Text(site.name)
                                .font(.system(size: 15, weight: .bold))
                                .foregroundStyle(AppTheme.textDark)
                            Spacer()
                            StatusBadge(status: site.status)
                        }
                        .padding(16)
                        .background(cardBackground)
                    }
                    .buttonStyle(.plain)
                }
            }
        }
    }

    // MARK: - Helpers

    private func sectionHeader(title: String, action: @escaping () -> Void) -> some View {
        HStack {
            Text(title)
                .font(.system(size: 17, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            Spacer()
            Button(action: action) {
                Text("Tout voir →")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(AppTheme.orange)
            }
        }
    }

    private var cardBackground: some View {
        RoundedRectangle(cornerRadius: 12)
            .fill(AppTheme.cardWhite)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(AppTheme.borderLight, lineWidth: 1)
            )
    }
}

#Preview {
    NavigationStack {
        DashboardView()
    }
}
