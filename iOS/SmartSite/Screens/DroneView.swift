import SwiftUI

/// Live drone feed + control screen (alpha: simulated feed).
/// Mirrors the Android Drone tab: stream panel, state card, command state machine.
struct DroneView: View {
    @StateObject private var drone = Repositories.drone
    @StateObject private var auth = Repositories.auth
    @State private var livePulsing = false

    private var role: UserRole { auth.currentUser.role }
    private var canControl: Bool { role == .admin || role == .droneOperator }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                header
                if let session = drone.session {
                    streamPanel(session: session)
                    stateCard(session: session)
                    if canControl {
                        commandsCard(session: session)
                    }
                } else {
                    noSessionCard
                }
            }
            .padding(16)
        }
        .background(AppTheme.cream)
    }

    // MARK: - Header

    private var header: some View {
        HStack(spacing: 12) {
            Image(systemName: "antenna.radiowaves.left.and.right")
                .font(.system(size: 22))
                .foregroundStyle(.white)
                .frame(width: 48, height: 48)
                .background(AppTheme.orange)
                .clipShape(RoundedRectangle(cornerRadius: 12))
            VStack(alignment: .leading, spacing: 2) {
                Text("Vue Drone")
                    .font(.system(size: 20, weight: .bold))
                    .foregroundStyle(AppTheme.textDark)
                Text(role == .droneOperator ? "Contrôle et gestion du drone" : "Flux vidéo du drone")
                    .font(.system(size: 13))
                    .foregroundStyle(AppTheme.textMuted)
            }
            Spacer()
        }
    }

    // MARK: - No session

    private var noSessionCard: some View {
        VStack(spacing: 16) {
            EmptyState(
                icon: "antenna.radiowaves.left.and.right",
                title: "Aucune session drone active",
                description: "Créez une session pour démarrer une mission drone."
            )
            Button {
                Repositories.drone.createSession(operatorEmail: auth.currentUser.email)
            } label: {
                Text("Créer une session")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(.white)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .background(AppTheme.orange)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .frame(maxWidth: .infinity)
        .background(AppTheme.cardWhite)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppTheme.borderLight, lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    // MARK: - Stream panel

    private func streamPanel(session: DroneSession) -> some View {
        ZStack {
            LinearGradient(
                colors: [Color(hex: "#1F2937"), Color(hex: "#0F172A")],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
            if !session.streamShared && role != .droneOperator {
                // Stream gated: the operator has not shared it.
                VStack(spacing: 8) {
                    Image(systemName: "wifi.slash")
                        .font(.system(size: 28))
                        .foregroundStyle(.white.opacity(0.6))
                    Text("Flux non disponible")
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundStyle(.white)
                    Text("Le droniste n'a pas partagé le flux")
                        .font(.system(size: 13))
                        .foregroundStyle(.white.opacity(0.6))
                }
            } else {
                VStack(spacing: 8) {
                    Image(systemName: "plus")
                        .font(.system(size: 34, weight: .light))
                        .foregroundStyle(.white.opacity(0.9))
                    Text("Flux vidéo simulé")
                        .font(.system(size: 13))
                        .foregroundStyle(.white.opacity(0.6))
                }
                VStack {
                    HStack {
                        Text(session.missionName)
                            .font(.system(size: 11, weight: .semibold))
                            .foregroundStyle(.white)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(.black.opacity(0.5))
                            .clipShape(Capsule())
                        Spacer()
                        livePill
                    }
                    Spacer()
                    HStack(alignment: .bottom) {
                        VStack(alignment: .leading, spacing: 2) {
                            Text("ALT: \(Int(session.altitude))m")
                            Text("GPS: \(String(format: "%.4f", session.gpsLat)), \(String(format: "%.4f", session.gpsLng))")
                        }
                        .font(.system(size: 11, weight: .medium, design: .monospaced))
                        .foregroundStyle(.white.opacity(0.85))
                        Spacer()
                        Button {
                            // Stub in the alpha — no capture is stored yet.
                        } label: {
                            Label("Capturer", systemImage: "camera.fill")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundStyle(.white)
                                .padding(.horizontal, 14)
                                .padding(.vertical, 8)
                                .background(AppTheme.orange)
                                .clipShape(RoundedRectangle(cornerRadius: 8))
                        }
                    }
                }
                .padding(12)
            }
        }
        .aspectRatio(16.0 / 9.0, contentMode: .fit)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var livePill: some View {
        HStack(spacing: 6) {
            Circle()
                .fill(.white)
                .frame(width: 6, height: 6)
            Text("LIVE")
                .font(.system(size: 11, weight: .bold))
                .foregroundStyle(.white)
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 4)
        .background(AppTheme.accentRed)
        .clipShape(Capsule())
        .opacity(livePulsing ? 0.35 : 1.0)
        .animation(.easeInOut(duration: 0.8).repeatForever(autoreverses: true), value: livePulsing)
        .onAppear { livePulsing = true }
    }

    // MARK: - State card

    private func stateCard(session: DroneSession) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("État du drone")
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            stateRow("Statut") {
                StatusBadge(status: session.status)
            }
            Divider()
            stateRow("Batterie") {
                Text("\(session.batteryLevel) %")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(batteryColor(session.batteryLevel))
            }
            Divider()
            stateRow("Altitude") {
                Text("\(Int(session.altitude)) m")
                    .font(.system(size: 14, weight: .semibold))
                    .foregroundStyle(AppTheme.textDark)
            }
            Divider()
            stateRow("Flux partagé") {
                Label(
                    session.streamShared ? "Oui" : "Non",
                    systemImage: session.streamShared ? "wifi" : "wifi.slash"
                )
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(session.streamShared ? AppTheme.accentGreen : AppTheme.textMuted)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.cardWhite)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppTheme.borderLight, lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func stateRow<Content: View>(_ label: String, @ViewBuilder value: () -> Content) -> some View {
        HStack {
            Text(label)
                .font(.system(size: 14))
                .foregroundStyle(AppTheme.textMuted)
            Spacer()
            value()
        }
    }

    private func batteryColor(_ level: Int) -> Color {
        if level > 50 { return AppTheme.accentGreen }
        if level > 20 { return AppTheme.accentAmber }
        return AppTheme.accentRed
    }

    // MARK: - Commands card

    private func commandsCard(session: DroneSession) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Commandes")
                .font(.system(size: 16, weight: .bold))
                .foregroundStyle(AppTheme.textDark)
            switch session.status {
            case .idle:
                primaryButton("Lancer mission", icon: "paperplane.fill") {
                    Repositories.drone.update { $0.status = .flying; $0.altitude = 25 }
                }
            case .flying:
                HStack(spacing: 10) {
                    primaryButton("Pause", icon: "pause.fill") {
                        Repositories.drone.update { $0.status = .paused }
                    }
                    rthButton
                }
            case .paused:
                HStack(spacing: 10) {
                    primaryButton("Reprendre", icon: "play.fill") {
                        Repositories.drone.update { $0.status = .flying }
                    }
                    rthButton
                }
            case .returning:
                primaryButton("Confirmer atterrissage", icon: "arrow.down.to.line") {
                    Repositories.drone.update { $0.status = .landed; $0.altitude = 0 }
                }
            case .landed:
                primaryButton("Réinitialiser", icon: "arrow.counterclockwise") {
                    Repositories.drone.update { $0.status = .idle }
                }
            }
            Toggle(isOn: Binding(
                get: { session.streamShared },
                set: { shared in Repositories.drone.update { $0.streamShared = shared } }
            )) {
                Label("Partager le flux", systemImage: "wifi")
                    .font(.system(size: 14, weight: .medium))
                    .foregroundStyle(AppTheme.textDark)
            }
            .tint(AppTheme.orange)
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(AppTheme.cardWhite)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppTheme.borderLight, lineWidth: 1))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private var rthButton: some View {
        Button {
            Repositories.drone.update { $0.status = .returning }
        } label: {
            Label("RTH", systemImage: "house.fill")
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(AppTheme.orange)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 11)
                .background(AppTheme.orange.opacity(0.12))
                .clipShape(RoundedRectangle(cornerRadius: 10))
                .overlay(RoundedRectangle(cornerRadius: 10).stroke(AppTheme.orange, lineWidth: 1))
        }
    }

    private func primaryButton(_ label: String, icon: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Label(label, systemImage: icon)
                .font(.system(size: 14, weight: .semibold))
                .foregroundStyle(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 11)
                .background(AppTheme.orange)
                .clipShape(RoundedRectangle(cornerRadius: 10))
        }
    }
}

#Preview {
    NavigationStack {
        DroneView()
    }
}
