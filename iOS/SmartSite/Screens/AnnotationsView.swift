import SwiftUI

private extension AnnotationType {
    var frenchLabel: String {
        switch self {
        case .text: return "Texte"
        case .arrow: return "Flèche"
        case .circle: return "Cercle"
        case .freehand: return "Dessin libre"
        }
    }

    var symbol: String {
        switch self {
        case .text: return "textformat"
        case .arrow: return "arrow.right"
        case .circle: return "circle"
        case .freehand: return "scribble"
        }
    }
}

private extension AnnotationSupport {
    var frenchLabel: String {
        switch self {
        case .plan: return "Plan"
        case .image: return "Image"
        case .droneCapture: return "Capture drone"
        }
    }
}

struct AnnotationsView: View {
    @StateObject private var annotationsRepo = Repositories.annotations
    @StateObject private var sitesRepo = Repositories.sites
    @State private var search = ""
    @State private var showCreate = false

    private var filteredAnnotations: [SiteAnnotation] {
        let query = search.trimmingCharacters(in: .whitespaces)
        guard !query.isEmpty else { return annotationsRepo.annotations }
        return annotationsRepo.annotations.filter {
            $0.content.lowercased().contains(query.lowercased())
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                header
                searchField
                if filteredAnnotations.isEmpty {
                    emptyState
                } else {
                    LazyVStack(spacing: 12) {
                        ForEach(filteredAnnotations) { annotation in
                            AnnotationCard(annotation: annotation)
                        }
                    }
                }
            }
            .padding(16)
        }
        .background(AppTheme.cream)
        .navigationTitle("Annotations")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showCreate) {
            AnnotationCreateView(sites: sitesRepo.sites)
        }
    }

    private var header: some View {
        HStack(alignment: .center) {
            VStack(alignment: .leading, spacing: 2) {
                Text("Annotations")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text("\(filteredAnnotations.count) annotation\(filteredAnnotations.count > 1 ? "s" : "")")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer()
            Button {
                showCreate = true
            } label: {
                HStack(spacing: 6) {
                    Image(systemName: "plus")
                    Text("Nouvelle")
                }
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(.white)
                .padding(.horizontal, 14)
                .padding(.vertical, 8)
                .background(AppTheme.orange)
                .clipShape(Capsule())
            }
        }
    }

    private var searchField: some View {
        HStack(spacing: 8) {
            Image(systemName: "magnifyingglass")
                .foregroundStyle(AppTheme.textMuted)
            TextField("Rechercher une annotation", text: $search)
                .font(.system(size: 15))
                .foregroundStyle(AppTheme.textDark)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 10)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }

    private var emptyState: some View {
        if annotationsRepo.annotations.isEmpty {
            EmptyState(
                icon: "pencil.tip",
                title: "Aucune annotation",
                description: "Créez votre première annotation pour commencer"
            )
        } else {
            EmptyState(
                icon: "magnifyingglass",
                title: "Aucun résultat",
                description: "Aucune annotation ne correspond à votre recherche"
            )
        }
    }
}

private struct AnnotationCard: View {
    let annotation: SiteAnnotation

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Image(systemName: annotation.type.symbol)
                .font(.system(size: 18))
                .foregroundStyle(annotation.color)
                .frame(width: 44, height: 44)
                .background(annotation.color.opacity(0.12))
                .clipShape(RoundedRectangle(cornerRadius: 12))
            VStack(alignment: .leading, spacing: 6) {
                Text(annotation.content.isBlank ? annotation.type.frenchLabel : annotation.content)
                    .font(.system(size: 15, weight: .medium))
                    .foregroundStyle(AppTheme.textDark)
                    .fixedSize(horizontal: false, vertical: true)
                HStack(spacing: 6) {
                    BadgePill(text: annotation.type.frenchLabel, color: annotation.color)
                    BadgePill(text: annotation.support.frenchLabel, color: AppTheme.slate)
                }
                if !annotation.zone.isBlank {
                    Text("Zone : \(annotation.zone)")
                        .font(.system(size: 13))
                        .foregroundStyle(AppTheme.textMuted)
                }
                Text("\(annotation.authorRole.frenchLabel) · \(annotation.createdDate.frenchFormatted)")
                    .font(.system(size: 12))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer(minLength: 0)
        }
        .padding(14)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }
}

private struct AnnotationCreateView: View {
    @Environment(\.dismiss) private var dismiss

    let sites: [Site]

    @State private var selectedSiteId: String?
    @State private var type: AnnotationType = .text
    @State private var support: AnnotationSupport = .plan
    @State private var content = ""
    @State private var zone = ""
    @State private var colorHex = AnnotationCreateView.presetColors[0]

    private static let presetColors = ["#EF4444", "#D4A03A", "#56A87A", "#5B89C8", "#7C6DB5", "#C96585"]

    var body: some View {
        NavigationStack {
            Form {
                Picker("Chantier *", selection: $selectedSiteId) {
                    Text("Sélectionner un chantier").tag(String?.none)
                    ForEach(sites) { site in
                        Text(site.name).tag(String?.some(site.id))
                    }
                }
                Picker("Type", selection: $type) {
                    ForEach(AnnotationType.allCases, id: \.self) { value in
                        Label(value.frenchLabel, systemImage: value.symbol).tag(value)
                    }
                }
                Picker("Support", selection: $support) {
                    ForEach(AnnotationSupport.allCases, id: \.self) { value in
                        Text(value.frenchLabel).tag(value)
                    }
                }
                TextField("Contenu", text: $content, axis: .vertical)
                    .lineLimit(3...6)
                TextField("Zone", text: $zone)
                VStack(alignment: .leading, spacing: 10) {
                    Text("Couleur")
                        .font(.system(size: 15))
                    HStack(spacing: 12) {
                        ForEach(Self.presetColors, id: \.self) { hex in
                            Circle()
                                .fill(Color(hex: hex))
                                .frame(width: 28, height: 28)
                                .padding(3)
                                .overlay(
                                    Circle()
                                        .stroke(colorHex == hex ? Color(hex: hex) : .clear, lineWidth: 2)
                                )
                                .onTapGesture {
                                    colorHex = hex
                                }
                        }
                    }
                }
                .padding(.vertical, 4)
            }
            .navigationTitle("Créer une annotation")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Créer") { create() }
                        .disabled(selectedSiteId == nil)
                }
            }
        }
    }

    private func create() {
        guard let siteId = selectedSiteId else { return }
        Repositories.annotations.create(
            type: type,
            content: content,
            support: support,
            zone: zone,
            colorHex: colorHex,
            siteId: siteId,
            authorRole: Repositories.auth.currentUser.role
        )
        dismiss()
    }
}

private extension String {
    var isBlank: Bool {
        trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}

#Preview {
    NavigationStack {
        AnnotationsView()
    }
}
