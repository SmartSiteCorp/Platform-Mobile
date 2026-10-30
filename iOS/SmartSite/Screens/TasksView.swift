import SwiftUI

/// Tasks list: search + status/priority filters, role-aware header
/// ("Mes tâches" for workers, who cannot create tasks).
struct TasksView: View {
    @StateObject private var taskRepo = Repositories.tasks
    @StateObject private var siteRepo = Repositories.sites
    @StateObject private var auth = Repositories.auth

    @State private var searchText = ""
    @State private var statusFilter: TaskStatus?
    @State private var priorityFilter: TaskPriority?
    @State private var showCreateSheet = false

    private var isWorker: Bool { auth.currentUser.role == .worker }

    private var filteredTasks: [SiteTask] {
        taskRepo.tasks.filter { task in
            let matchesSearch = searchText.isEmpty
                || task.title.localizedCaseInsensitiveContains(searchText)
            let matchesStatus = statusFilter == nil || task.status == statusFilter
            let matchesPriority = priorityFilter == nil || task.priority == priorityFilter
            return matchesSearch && matchesStatus && matchesPriority
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                header
                filters
                if filteredTasks.isEmpty {
                    EmptyState(
                        icon: "list.clipboard",
                        title: "Aucune tâche trouvée",
                        description: "Modifiez votre recherche ou vos filtres"
                    )
                } else {
                    ForEach(filteredTasks) { task in
                        taskCard(task)
                    }
                }
            }
            .padding(16)
        }
        .background(AppTheme.cream)
        .sheet(isPresented: $showCreateSheet) {
            CreateTaskSheet(sites: siteRepo.sites) { title, siteId, description, priority, zone, assignedTo, dueDate in
                taskRepo.create(
                    title: title,
                    siteId: siteId,
                    description: description,
                    priority: priority,
                    zone: zone,
                    assignedTo: assignedTo,
                    dueDate: dueDate
                )
            }
        }
    }

    // MARK: - Header

    private var header: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 2) {
                Text(isWorker ? "Mes tâches" : "Tâches")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text("\(filteredTasks.count) tâche(s)")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer()
            if !isWorker {
                Button {
                    showCreateSheet = true
                } label: {
                    Label("Nouvelle tâche", systemImage: "plus")
                        .font(.system(size: 14, weight: .semibold))
                        .foregroundStyle(.white)
                        .padding(.horizontal, 14)
                        .padding(.vertical, 9)
                        .background(AppTheme.orange)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                }
            }
        }
    }

    // MARK: - Filters

    private var filters: some View {
        VStack(spacing: 10) {
            HStack(spacing: 8) {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(AppTheme.textMuted)
                TextField("Rechercher une tâche", text: $searchText)
                    .font(.system(size: 14))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(AppTheme.cardWhite)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(AppTheme.borderLight, lineWidth: 1)
            )

            HStack(spacing: 10) {
                Menu {
                    Button {
                        statusFilter = nil
                    } label: {
                        Label("Tous", systemImage: statusFilter == nil ? "checkmark" : "")
                    }
                    ForEach(TaskStatus.allCases, id: \.self) { status in
                        Button {
                            statusFilter = status
                        } label: {
                            Label(
                                Self.statusLabel(status),
                                systemImage: statusFilter == status ? "checkmark" : ""
                            )
                        }
                    }
                } label: {
                    filterChip(title: statusFilter.map(Self.statusLabel) ?? "Tous")
                }

                Menu {
                    Button {
                        priorityFilter = nil
                    } label: {
                        Label("Toutes", systemImage: priorityFilter == nil ? "checkmark" : "")
                    }
                    ForEach(TaskPriority.allCases, id: \.self) { priority in
                        Button {
                            priorityFilter = priority
                        } label: {
                            Label(
                                Self.priorityLabel(priority),
                                systemImage: priorityFilter == priority ? "checkmark" : ""
                            )
                        }
                    }
                } label: {
                    filterChip(title: priorityFilter.map(Self.priorityLabel) ?? "Toutes")
                }
            }
        }
    }

    private func filterChip(title: String) -> some View {
        HStack(spacing: 6) {
            Text(title)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(AppTheme.textDark)
            Image(systemName: "chevron.down")
                .font(.system(size: 10, weight: .semibold))
                .foregroundStyle(AppTheme.textMuted)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 10))
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }

    // MARK: - Cards

    private func taskCard(_ task: SiteTask) -> some View {
        NavigationLink(value: AppRoute.taskDetail(task.id)) {
            VStack(alignment: .leading, spacing: 8) {
                HStack(alignment: .top) {
                    Text(task.title)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundStyle(AppTheme.textDark)
                        .multilineTextAlignment(.leading)
                    Spacer()
                    Image(systemName: "chevron.right")
                        .font(.system(size: 13, weight: .semibold))
                        .foregroundStyle(AppTheme.textMuted)
                }
                Text(siteRepo.site(byId: task.siteId)?.name ?? "Chantier inconnu")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
                HStack(spacing: 6) {
                    StatusBadge(status: task.status)
                    PriorityBadge(priority: task.priority)
                }
                HStack(spacing: 14) {
                    Text("Zone : \(task.zone)")
                        .font(.system(size: 12))
                        .foregroundStyle(AppTheme.textMuted)
                    Label(task.dueDate.frenchFormatted, systemImage: "clock")
                        .font(.system(size: 12))
                        .foregroundStyle(AppTheme.textMuted)
                }
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
        .buttonStyle(.plain)
    }

    // MARK: - Labels

    static func statusLabel(_ status: TaskStatus) -> String {
        switch status {
        case .pending: return "En attente"
        case .inProgress: return "En cours"
        case .completed: return "Terminé"
        case .blocked: return "Bloqué"
        }
    }

    static func priorityLabel(_ priority: TaskPriority) -> String {
        switch priority {
        case .low: return "Faible"
        case .medium: return "Moyen"
        case .high: return "Élevé"
        case .urgent: return "Urgent"
        }
    }
}

// MARK: - Create task sheet

private struct CreateTaskSheet: View {
    let sites: [Site]
    let onCreate: (String, String, String, TaskPriority, String, String, Date) -> Void

    @Environment(\.dismiss) private var dismiss

    @State private var title = ""
    @State private var selectedSiteId: String?
    @State private var taskDescription = ""
    @State private var priority: TaskPriority = .medium
    @State private var zone = ""
    @State private var assignedTo = ""
    @State private var dueDate = Date()

    private var canConfirm: Bool {
        !title.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && selectedSiteId != nil
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Titre*", text: $title)
                    Picker("Chantier*", selection: $selectedSiteId) {
                        Text("Sélectionner un chantier").tag(nil as String?)
                        ForEach(sites) { site in
                            Text(site.name).tag(site.id as String?)
                        }
                    }
                    TextField("Description", text: $taskDescription, axis: .vertical)
                        .lineLimit(3...6)
                    Picker("Priorité", selection: $priority) {
                        ForEach(TaskPriority.allCases, id: \.self) { priority in
                            Text(TasksView.priorityLabel(priority)).tag(priority)
                        }
                    }
                    TextField("Zone", text: $zone, prompt: Text("ex: Bâtiment A"))
                    TextField("Assigné à (email)", text: $assignedTo)
                        .keyboardType(.emailAddress)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                    DatePicker("Date limite", selection: $dueDate, displayedComponents: .date)
                }
            }
            .navigationTitle("Créer une tâche")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Créer") {
                        guard let siteId = selectedSiteId else { return }
                        onCreate(
                            title.trimmingCharacters(in: .whitespacesAndNewlines),
                            siteId,
                            taskDescription.trimmingCharacters(in: .whitespacesAndNewlines),
                            priority,
                            zone.trimmingCharacters(in: .whitespacesAndNewlines),
                            assignedTo.trimmingCharacters(in: .whitespacesAndNewlines),
                            dueDate
                        )
                        dismiss()
                    }
                    .disabled(!canConfirm)
                    .tint(AppTheme.orange)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        TasksView()
    }
}
