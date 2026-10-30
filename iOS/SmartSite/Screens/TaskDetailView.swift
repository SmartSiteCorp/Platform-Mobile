import SwiftUI
import PhotosUI

/// Task detail: header card, assignee workflow (start / validate / report a
/// blockage) and the validation / blocking evidence cards once submitted.
struct TaskDetailView: View {
    let taskId: String

    @StateObject private var taskRepo = Repositories.tasks
    @StateObject private var siteRepo = Repositories.sites
    @StateObject private var auth = Repositories.auth

    @State private var showValidationSheet = false
    @State private var showBlockingSheet = false

    private var task: SiteTask? {
        taskRepo.tasks.first(where: { $0.id == taskId })
    }

    var body: some View {
        ScrollView {
            if let task {
                VStack(alignment: .leading, spacing: 16) {
                    headerCard(task)
                    workflowButtons(task)
                    if task.status == .completed {
                        validationCard(task)
                    }
                    if task.status == .blocked {
                        blockingCard(task)
                    }
                }
                .padding(16)
            } else {
                EmptyState(
                    icon: "exclamationmark.triangle",
                    title: "Tâche introuvable",
                    description: "Cette tâche n'existe pas ou a été supprimée"
                )
                .padding(16)
            }
        }
        .background(AppTheme.cream)
        .sheet(isPresented: $showValidationSheet) {
            ValidationSheet { comment, photo in
                guard var updated = task else { return }
                updated.status = .completed
                updated.completionComment = comment
                updated.completionPhoto = photo
                taskRepo.update(updated)
            }
        }
        .sheet(isPresented: $showBlockingSheet) {
            BlockingSheet { reason, photo in
                guard var updated = task else { return }
                updated.status = .blocked
                updated.blockingReason = reason
                updated.blockingPhoto = photo
                taskRepo.update(updated)
            }
        }
    }

    // MARK: - Header card

    private func headerCard(_ task: SiteTask) -> some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(task.title)
                .font(.system(size: 20, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            Label(
                siteRepo.site(byId: task.siteId)?.name ?? "Chantier inconnu",
                systemImage: "mappin"
            )
            .font(.system(size: 13))
            .foregroundStyle(AppTheme.textMuted)

            HStack(spacing: 6) {
                StatusBadge(status: task.status)
                PriorityBadge(priority: task.priority)
                BadgePill(text: "Zone : \(task.zone)", color: AppTheme.textMuted)
            }

            VStack(alignment: .leading, spacing: 6) {
                Label("Échéance : \(task.dueDate.frenchFormatted)", systemImage: "clock")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
                Label("Assigné à : \(task.assignedTo)", systemImage: "person.fill")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
            }

            if !task.description.isEmpty {
                Divider()
                Text(task.description)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textDark)
                    .fixedSize(horizontal: false, vertical: true)
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

    // MARK: - Workflow

    @ViewBuilder
    private func workflowButtons(_ task: SiteTask) -> some View {
        if task.assignedTo == auth.currentUser.email && task.status != .completed {
            VStack(spacing: 10) {
                if task.status == .pending {
                    Button {
                        var updated = task
                        updated.status = .inProgress
                        taskRepo.update(updated)
                    } label: {
                        Label("Démarrer la tâche", systemImage: "play.fill")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(AppTheme.accentBlue)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                }
                if task.status == .pending || task.status == .inProgress {
                    Button {
                        showValidationSheet = true
                    } label: {
                        Label("Valider", systemImage: "checkmark.circle.fill")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundStyle(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(AppTheme.accentGreen)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                    }
                    Button {
                        showBlockingSheet = true
                    } label: {
                        Label("Signaler un blocage", systemImage: "exclamationmark.triangle.fill")
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundStyle(AppTheme.accentRed)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(AppTheme.cardWhite)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                            .overlay(
                                RoundedRectangle(cornerRadius: 12)
                                    .stroke(AppTheme.accentRed, lineWidth: 1)
                            )
                    }
                }
            }
        }
    }

    // MARK: - Evidence cards

    private func validationCard(_ task: SiteTask) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Label("Validation", systemImage: "checkmark.seal.fill")
                .font(.system(size: 15, weight: .bold))
                .foregroundStyle(AppTheme.accentGreen)
            if let comment = task.completionComment, !comment.isEmpty {
                Text(comment)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textDark)
            }
            if let data = task.completionPhoto, let image = UIImage(data: data) {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(height: 180)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.accentGreen.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.accentGreen.opacity(0.4), lineWidth: 1)
        )
    }

    private func blockingCard(_ task: SiteTask) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Label("Blocage signalé", systemImage: "exclamationmark.triangle.fill")
                .font(.system(size: 15, weight: .bold))
                .foregroundStyle(AppTheme.accentRed)
            if let reason = task.blockingReason, !reason.isEmpty {
                Text(reason)
                    .font(.system(size: 14))
                    .foregroundStyle(AppTheme.textDark)
            }
            if let data = task.blockingPhoto, let image = UIImage(data: data) {
                Image(uiImage: image)
                    .resizable()
                    .scaledToFill()
                    .frame(maxWidth: .infinity)
                    .frame(height: 180)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
        }
        .padding(14)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.accentRed.opacity(0.08))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.accentRed.opacity(0.4), lineWidth: 1)
        )
    }
}

// MARK: - Validation sheet

private struct ValidationSheet: View {
    let onConfirm: (String, Data?) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var comment = ""
    @State private var photoData: Data?

    private var canConfirm: Bool {
        !comment.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty && photoData != nil
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Commentaire*", text: $comment, axis: .vertical)
                        .lineLimit(3...6)
                }
                Section {
                    ProofPhotoPicker(data: $photoData)
                } header: {
                    Text("Photo de preuve*")
                }
            }
            .navigationTitle("Valider la tâche")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Valider") {
                        onConfirm(
                            comment.trimmingCharacters(in: .whitespacesAndNewlines),
                            photoData
                        )
                        dismiss()
                    }
                    .disabled(!canConfirm)
                    .tint(AppTheme.accentGreen)
                }
            }
        }
    }
}

// MARK: - Blocking sheet

private struct BlockingSheet: View {
    let onConfirm: (String, Data?) -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var reason = ""
    @State private var photoData: Data?

    private var canConfirm: Bool {
        !reason.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Raison*", text: $reason, axis: .vertical)
                        .lineLimit(3...6)
                }
                Section {
                    ProofPhotoPicker(data: $photoData)
                } header: {
                    Text("Photo (optionnelle)")
                }
            }
            .navigationTitle("Signaler un blocage")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Annuler") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Signaler") {
                        onConfirm(
                            reason.trimmingCharacters(in: .whitespacesAndNewlines),
                            photoData
                        )
                        dismiss()
                    }
                    .disabled(!canConfirm)
                    .tint(AppTheme.accentRed)
                }
            }
        }
    }
}

// MARK: - Photo picker (gallery + camera)

private struct ProofPhotoPicker: View {
    @Binding var data: Data?

    @State private var selectedItem: PhotosPickerItem?
    @State private var showCamera = false

    private var cameraAvailable: Bool {
        UIImagePickerController.isSourceTypeAvailable(.camera)
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            HStack(spacing: 10) {
                PhotosPicker(selection: $selectedItem, matching: .images) {
                    Label("Galerie", systemImage: "photo.on.rectangle")
                        .font(.system(size: 14, weight: .medium))
                }
                Button {
                    showCamera = true
                } label: {
                    Label("Caméra", systemImage: "camera.fill")
                        .font(.system(size: 14, weight: .medium))
                }
                .disabled(!cameraAvailable)
            }
            if let data, let image = UIImage(data: data) {
                ZStack(alignment: .topTrailing) {
                    Image(uiImage: image)
                        .resizable()
                        .scaledToFill()
                        .frame(maxWidth: .infinity)
                        .frame(height: 160)
                        .clipShape(RoundedRectangle(cornerRadius: 10))
                    Button {
                        self.data = nil
                        selectedItem = nil
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.system(size: 22))
                            .foregroundStyle(.white)
                            .shadow(radius: 2)
                            .padding(8)
                    }
                }
            }
        }
        .onChange(of: selectedItem) { _, newItem in
            Task {
                if let loaded = try? await newItem?.loadTransferable(type: Data.self) {
                    data = loaded
                }
            }
        }
        .sheet(isPresented: $showCamera) {
            CameraPicker(imageData: $data)
                .ignoresSafeArea()
        }
    }
}

// MARK: - Camera (UIImagePickerController bridge)

private struct CameraPicker: UIViewControllerRepresentable {
    @Binding var imageData: Data?
    @Environment(\.dismiss) private var dismiss

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    final class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        private let parent: CameraPicker

        init(_ parent: CameraPicker) {
            self.parent = parent
        }

        func imagePickerController(
            _ picker: UIImagePickerController,
            didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]
        ) {
            if let image = info[.originalImage] as? UIImage {
                parent.imageData = image.jpegData(compressionQuality: 0.8)
            }
            parent.dismiss()
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.dismiss()
        }
    }
}

#Preview {
    NavigationStack {
        TaskDetailView(taskId: "task-menuiseries")
    }
}
