import SwiftUI

/// Profile sheet: identity header + editable phone / specialty form.
/// Mirrors the Android Profile screen; saves through `Repositories.auth`.
struct ProfileView: View {
    @ObservedObject private var auth = Repositories.auth

    @State private var phone: String
    @State private var specialty: String
    @State private var isSaving = false

    init() {
        let user = Repositories.auth.currentUser
        _phone = State(initialValue: user.phone)
        _specialty = State(initialValue: user.specialty)
    }

    private var roleColor: Color {
        switch auth.currentUser.role {
        case .admin: return AppTheme.accentPurple
        case .architect: return AppTheme.accentBlue
        case .worker: return AppTheme.accentAmber
        case .projectManager: return AppTheme.accentGreen
        case .droneOperator: return AppTheme.accentRose
        }
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 16) {
                headerCard
                formCard
            }
            .padding(16)
        }
        .background(AppTheme.cream)
        .navigationTitle("Mon profil")
        .navigationBarTitleDisplayMode(.inline)
    }

    // MARK: - Header

    private var headerCard: some View {
        VStack(spacing: 12) {
            Text(auth.currentUser.fullName.prefix(1).uppercased())
                .font(.system(size: 26, weight: .bold))
                .foregroundStyle(.white)
                .frame(width: 64, height: 64)
                .background(roleColor)
                .clipShape(RoundedRectangle(cornerRadius: 16))

            Text(auth.currentUser.fullName)
                .font(.system(size: 20, weight: .bold))
                .foregroundStyle(AppTheme.textDark)

            Label(auth.currentUser.role.frenchLabel, systemImage: "shield.fill")
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(roleColor)
                .padding(.horizontal, 12)
                .padding(.vertical, 5)
                .background(roleColor.opacity(0.12))
                .clipShape(Capsule())
        }
        .frame(maxWidth: .infinity)
        .padding(20)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }

    // MARK: - Form

    private var formCard: some View {
        VStack(alignment: .leading, spacing: 16) {
            field(title: "Email") {
                TextField("", text: .constant(auth.currentUser.email))
                    .disabled(true)
                    .foregroundStyle(AppTheme.textMuted)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(Color(hex: "#EDEBE6"))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
            }

            field(title: "Téléphone") {
                TextField("06 00 00 00 00", text: $phone)
                    .keyboardType(.phonePad)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(AppTheme.cream)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(AppTheme.borderLight, lineWidth: 1)
                    )
            }

            field(title: "Spécialité") {
                TextField("ex: Maçonnerie, Plomberie...", text: $specialty)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 10)
                    .background(AppTheme.cream)
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(AppTheme.borderLight, lineWidth: 1)
                    )
            }

            Button {
                save()
            } label: {
                HStack(spacing: 8) {
                    if isSaving {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Image(systemName: "square.and.arrow.down")
                    }
                    Text(isSaving ? "Enregistrement..." : "Enregistrer")
                        .font(.system(size: 16, weight: .semibold))
                }
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 13)
                .background(AppTheme.orange)
                .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .disabled(isSaving)
            .opacity(isSaving ? 0.8 : 1)
        }
        .padding(16)
        .background(AppTheme.cardWhite)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(AppTheme.borderLight, lineWidth: 1)
        )
    }

    private func field<Content: View>(title: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 6) {
            Text(title.uppercased())
                .font(.system(size: 11, weight: .medium))
                .foregroundStyle(AppTheme.textMuted)
            content()
        }
    }

    // MARK: - Save

    private func save() {
        guard !isSaving else { return }
        isSaving = true
        Task {
            try? await Task.sleep(nanoseconds: 600_000_000)
            var user = auth.currentUser
            user.phone = phone
            user.specialty = specialty
            Repositories.auth.update(user)
            isSaving = false
        }
    }
}

#Preview {
    NavigationStack {
        ProfileView()
    }
}
