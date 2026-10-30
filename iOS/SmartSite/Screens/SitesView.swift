import SwiftUI

/// Sites tab: searchable/filterable list of chantiers + creation sheet.
struct SitesView: View {
    @StateObject private var siteRepo = Repositories.sites
    @State private var searchText = ""
    @State private var statusFilter: SiteStatus?
    @State private var showCreateSheet = false

    private var filteredSites: [Site] {
        siteRepo.sites.filter { site in
            let matchesSearch = searchText.isEmpty
                || site.name.localizedCaseInsensitiveContains(searchText)
                || site.address.localizedCaseInsensitiveContains(searchText)
            let matchesStatus = statusFilter.map { $0 == site.status } ?? true
            return matchesSearch && matchesStatus
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                header
                searchAndFilter
                if filteredSites.isEmpty {
                    EmptyState(
                        icon: "building.2",
                        title: "Aucun chantier trouvé",
                        description: "Créez votre premier chantier pour commencer"
                    )
                    .padding(.top, 24)
                } else {
                    ForEach(filteredSites) { site in
                        NavigationLink(value: AppRoute.siteDetail(site.id)) {
                            siteCard(site)
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(16)
        }
        .background(AppTheme.cream)
        .sheet(isPresented: $showCreateSheet) {
            CreateSiteSheet()
        }
    }

    private var header: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Chantiers")
                    .font(.system(size: 26, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text("\(siteRepo.sites.count) chantier(s)")
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer()
            Button {
                showCreateSheet = true
            } label: {
                Label("Nouveau chantier", systemImage: "plus")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(.white)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 10)
                    .background(AppTheme.orange)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
    }

    private var searchAndFilter: some View {
        HStack(spacing: 10) {
            HStack(spacing: 8) {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(AppTheme.textMuted)
                TextField("Rechercher un chantier...", text: $searchText)
                    .font(.system(size: 14))
            }
            .padding(.horizontal, 12)
            .padding(.vertical, 10)
            .background(AppTheme.cardWhite)
            .clipShape(RoundedRectangle(cornerRadius: 10))
            .overlay(
                RoundedRectangle(cornerRadius: 10)
                    .stroke(AppTheme.borderLight, lineWidth: 1)
            )

            Menu {
                Button("Tous") { statusFilter = nil }
                ForEach(SiteStatus.allCases, id: \.self) { status in
                    Button(status.frenchLabel) { statusFilter = status }
                }
            } label: {
                HStack(spacing: 6) {
                    Text(statusFilter?.frenchLabel ?? "Tous")
                        .font(.system(size: 13, weight: .medium))
                        .lineLimit(1)
                    Image(systemName: "chevron.down")
                        .font(.system(size: 10, weight: .semibold))
                }
                .foregroundStyle(AppTheme.textDark)
                .padding(.horizontal, 12)
                .padding(.vertical, 10)
                .background(AppTheme.cardWhite)
                .clipShape(RoundedRectangle(cornerRadius: 10))
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(AppTheme.borderLight, lineWidth: 1)
                )
            }
        }
    }

    private func siteCard(_ site: Site) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            Rectangle()
                .fill(.clear)
                .aspectRatio(2.2, contentMode: .fit)
                .overlay(SiteCoverView(cover: site.cover))
                .clipShape(
                    UnevenRoundedRectangle(
                        topLeadingRadius: 12,
                        bottomLeadingRadius: 0,
                        bottomTrailingRadius: 0,
                        topTrailingRadius: 12
                    )
                )
            VStack(alignment: .leading, spacing: 8) {
                Text(site.name)
                    .font(.system(size: 17, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                HStack(spacing: 6) {
                    Image(systemName: "mappin")
                    Text(site.address)
                        .lineLimit(1)
                }
                .font(.system(size: 13))
                .foregroundStyle(AppTheme.textMuted)
                HStack {
                    StatusBadge(status: site.status)
                    Spacer()
                    HStack(spacing: 6) {
                        Image(systemName: "calendar")
                        Text(site.startDate.frenchFormatted)
                    }
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
                }
            }
            .padding(14)
        }
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }
}

/// Sheet presented from "Nouveau chantier". Creates the site immediately
/// in the mock repository (status defaults to .planning).
private struct CreateSiteSheet: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var siteRepo = Repositories.sites
    @State private var name = ""
    @State private var address = ""
    @State private var siteDescription = ""
    @State private var startDate = Date()
    @State private var endDate = Date()

    private var canCreate: Bool {
        !name.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    field("Nom*", text: $name, prompt: "Nom du chantier")
                    field("Adresse", text: $address, prompt: "Adresse du chantier")
                    field("Description", text: $siteDescription, prompt: "Description", axis: .vertical)
                    VStack(alignment: .leading, spacing: 8) {
                        DatePicker("Date début", selection: $startDate, displayedComponents: .date)
                        DatePicker("Date fin", selection: $endDate, displayedComponents: .date)
                    }
                    .font(.system(size: 15))
                    .tint(AppTheme.orange)
                    .padding(12)
                    .background(AppTheme.cardWhite)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                    .overlay(
                        RoundedRectangle(cornerRadius: 10)
                            .stroke(AppTheme.borderLight, lineWidth: 1)
                    )

                    Button {
                        siteRepo.create(
                            name: name.trimmingCharacters(in: .whitespacesAndNewlines),
                            address: address,
                            description: siteDescription,
                            startDate: startDate,
                            endDate: endDate
                        )
                        dismiss()
                    } label: {
                        Text("Créer le chantier")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                            .background(canCreate ? AppTheme.orange : AppTheme.orange.opacity(0.4))
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    .disabled(!canCreate)
                }
                .padding(16)
            }
            .background(AppTheme.cream)
            .navigationTitle("Créer un chantier")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { dismiss() }
                        .tint(AppTheme.orange)
                }
            }
        }
    }

    private func field(_ label: String, text: Binding<String>, prompt: String, axis: Axis = .horizontal) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(label)
                .font(.system(size: 13, weight: .medium))
                .foregroundStyle(AppTheme.textDark)
            TextField(prompt, text: text, axis: axis)
                .font(.system(size: 15))
                .padding(12)
                .background(AppTheme.cardWhite)
                .clipShape(RoundedRectangle(cornerRadius: 10))
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(AppTheme.borderLight, lineWidth: 1)
                )
        }
    }
}

private extension SiteStatus {
    var frenchLabel: String {
        switch self {
        case .planning: return "Planification"
        case .inProgress: return "En cours"
        case .completed: return "Terminé"
        case .onHold: return "En pause"
        }
    }
}

#Preview {
    NavigationStack {
        SitesView()
    }
}
