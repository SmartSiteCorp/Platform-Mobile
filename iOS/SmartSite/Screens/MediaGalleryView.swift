import SwiftUI
import PhotosUI

private extension MediaType {
    var frenchLabel: String {
        switch self {
        case .photo: return "Photo"
        case .droneCapture: return "Capture drone"
        case .lidarScan: return "Scan LiDAR"
        case .plan: return "Plan"
        case .document: return "Document"
        }
    }

    var symbol: String {
        switch self {
        case .photo: return "camera.fill"
        case .droneCapture: return "camera.fill"
        case .lidarScan: return "waveform.path.ecg"
        case .plan: return "photo"
        case .document: return "doc.text.fill"
        }
    }
}

/// Médiathèque : grille 2 colonnes de tous les médias, avec recherche,
/// filtre par type, détail en sheet et upload via PhotosPicker.
struct MediaGalleryView: View {
    @StateObject private var mediaRepo = Repositories.media
    @StateObject private var sitesRepo = Repositories.sites

    @State private var searchText = ""
    @State private var selectedType: MediaType?
    @State private var selectedItem: MediaItem?
    @State private var showUpload = false

    private var filteredMedia: [MediaItem] {
        mediaRepo.media.filter { item in
            let matchesType = selectedType == nil || item.type == selectedType
            let matchesSearch = searchText.isEmpty
                || item.caption.localizedCaseInsensitiveContains(searchText)
            return matchesType && matchesSearch
        }
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                header
                filters
                if filteredMedia.isEmpty {
                    EmptyState(
                        icon: "photo.on.rectangle",
                        title: "Aucun média",
                        description: "Uploadez vos premiers fichiers"
                    )
                    .padding(.top, 32)
                } else {
                    grid
                }
            }
            .padding(16)
        }
        .background(AppTheme.cream.ignoresSafeArea())
        .navigationBarTitleDisplayMode(.inline)
        .sheet(item: $selectedItem) { item in
            MediaDetailSheet(item: item)
        }
        .sheet(isPresented: $showUpload) {
            UploadMediaSheet(sites: sitesRepo.sites)
        }
    }

    // MARK: - Header

    private var header: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 4) {
                Text("Médiathèque")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text("\(filteredMedia.count) fichier(s)")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer()
            Button {
                showUpload = true
            } label: {
                Label("Uploader", systemImage: "plus")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(.white)
                    .padding(.horizontal, 14)
                    .padding(.vertical, 9)
                    .background(AppTheme.orange)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
    }

    // MARK: - Filters

    private var filters: some View {
        HStack(spacing: 10) {
            HStack(spacing: 8) {
                Image(systemName: "magnifyingglass")
                    .foregroundStyle(AppTheme.textMuted)
                TextField("Rechercher une légende…", text: $searchText)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textDark)
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
                Button {
                    selectedType = nil
                } label: {
                    Label("Tous types", systemImage: "square.grid.2x2")
                }
                ForEach(MediaType.allCases, id: \.self) { type in
                    Button {
                        selectedType = type
                    } label: {
                        Label(type.frenchLabel, systemImage: type.symbol)
                    }
                }
            } label: {
                HStack(spacing: 6) {
                    Image(systemName: selectedType?.symbol ?? "line.3.horizontal.decrease.circle")
                    Text(selectedType?.frenchLabel ?? "Tous types")
                        .lineLimit(1)
                }
                .font(.system(size: 14, weight: .medium))
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

    // MARK: - Grid

    private var grid: some View {
        LazyVGrid(
            columns: [
                GridItem(.flexible(), spacing: 12),
                GridItem(.flexible(), spacing: 12)
            ],
            spacing: 12
        ) {
            ForEach(filteredMedia) { item in
                mediaCard(item)
            }
        }
    }

    private func mediaCard(_ item: MediaItem) -> some View {
        MediaVisualView(item: item)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .aspectRatio(1, contentMode: .fill)
            .clipped()
            .overlay(alignment: .bottom) {
                ZStack(alignment: .bottomLeading) {
                    LinearGradient(
                        colors: [.clear, .black.opacity(0.65)],
                        startPoint: .top,
                        endPoint: .bottom
                    )
                    Text(item.caption.isEmpty ? item.type.frenchLabel : item.caption)
                        .font(.system(size: 12, weight: .medium))
                        .foregroundStyle(.white)
                        .lineLimit(2)
                        .padding(10)
                }
            }
            .overlay(alignment: .topTrailing) {
                HStack(spacing: 4) {
                    Image(systemName: item.type.symbol)
                    Text(item.type.frenchLabel)
                }
                .font(.system(size: 10, weight: .medium))
                .foregroundStyle(.white)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(.black.opacity(0.5))
                .clipShape(Capsule())
                .padding(8)
            }
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(AppTheme.borderLight, lineWidth: 1)
            )
            .contentShape(RoundedRectangle(cornerRadius: 14))
            .onTapGesture {
                selectedItem = item
            }
    }
}

// MARK: - Detail sheet

private struct MediaDetailSheet: View {
    let item: MediaItem
    @Environment(\.dismiss) private var dismiss

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    MediaVisualView(item: item)
                        .frame(maxWidth: .infinity)
                        .frame(height: 280)
                        .clipped()
                        .clipShape(RoundedRectangle(cornerRadius: 14))

                    Text(item.caption.isEmpty ? item.type.frenchLabel : item.caption)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundStyle(AppTheme.textDark)

                    HStack(spacing: 4) {
                        Image(systemName: item.type.symbol)
                        Text(item.type.frenchLabel)
                    }
                    .font(.system(size: 12, weight: .medium))
                    .foregroundStyle(.white)
                    .padding(.horizontal, 10)
                    .padding(.vertical, 5)
                    .background(.black.opacity(0.5))
                    .clipShape(Capsule())

                    Text("Ajouté le \(item.createdDate.frenchFormatted)")
                        .font(.system(size: 13))
                        .foregroundStyle(AppTheme.textMuted)
                }
                .padding(16)
            }
            .background(AppTheme.cream.ignoresSafeArea())
            .navigationTitle("Média")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Fermer") { dismiss() }
                        .foregroundStyle(AppTheme.orange)
                }
            }
        }
        .presentationDetents([.medium, .large])
    }
}

// MARK: - Upload sheet

private struct UploadMediaSheet: View {
    let sites: [Site]

    @Environment(\.dismiss) private var dismiss

    @State private var pickedItem: PhotosPickerItem?
    @State private var pickedData: Data?
    @State private var selectedSiteId = ""
    @State private var selectedType: MediaType = .photo
    @State private var caption = ""

    private var canAdd: Bool {
        pickedData != nil && !selectedSiteId.isEmpty
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Fichier") {
                    PhotosPicker(selection: $pickedItem, matching: .images) {
                        if let pickedData, let uiImage = UIImage(data: pickedData) {
                            HStack(spacing: 12) {
                                Image(uiImage: uiImage)
                                    .resizable()
                                    .scaledToFill()
                                    .frame(width: 44, height: 44)
                                    .clipped()
                                    .clipShape(RoundedRectangle(cornerRadius: 8))
                                Text("Fichier sélectionné")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundStyle(AppTheme.textDark)
                            }
                        } else {
                            Label("Choisir un fichier", systemImage: "photo.badge.plus")
                                .font(.system(size: 14, weight: .medium))
                                .foregroundStyle(AppTheme.orange)
                        }
                    }
                    .onChange(of: pickedItem) { _, newItem in
                        Task {
                            pickedData = try? await newItem?.loadTransferable(type: Data.self)
                        }
                    }
                }

                Section("Détails") {
                    Picker("Chantier*", selection: $selectedSiteId) {
                        Text("Sélectionner un chantier").tag("")
                        ForEach(sites) { site in
                            Text(site.name).tag(site.id)
                        }
                    }
                    Picker("Type", selection: $selectedType) {
                        ForEach(MediaType.allCases, id: \.self) { type in
                            Label(type.frenchLabel, systemImage: type.symbol).tag(type)
                        }
                    }
                    TextField("Légende", text: $caption)
                }
            }
            .navigationTitle("Ajouter un média")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Annuler") { dismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Ajouter") {
                        Repositories.media.create(
                            imageData: pickedData,
                            type: selectedType,
                            caption: caption.trimmingCharacters(in: .whitespacesAndNewlines),
                            siteId: selectedSiteId
                        )
                        dismiss()
                    }
                    .fontWeight(.semibold)
                    .disabled(!canAdd)
                }
            }
        }
    }
}

#Preview {
    NavigationStack {
        MediaGalleryView()
    }
}
