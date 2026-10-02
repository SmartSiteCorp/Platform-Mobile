package com.smartsite.app.ui.screens.drone

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsite.app.data.mock.DroneRepository
import com.smartsite.app.data.mock.MockAuthRepository
import com.smartsite.app.data.model.DroneSession
import com.smartsite.app.data.model.DroneStatus
import com.smartsite.app.data.model.UserRole
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.AccentAmber
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import java.util.Locale

@Composable
fun DroneViewScreen() {
    val session by DroneRepository.session.collectAsState()
    val user by MockAuthRepository.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DroneHeader(isOperator = user.role == UserRole.DRONE_OPERATOR)

        val current = session
        if (current == null) {
            NoSessionCard(onCreateSession = { DroneRepository.createSession(user.email) })
        } else {
            VideoPanel(
                session = current,
                streamAvailable = current.streamShared || user.role == UserRole.DRONE_OPERATOR
            )
            DroneStateCard(session = current)
            if (user.role == UserRole.ADMIN || user.role == UserRole.DRONE_OPERATOR) {
                CommandsCard(session = current)
            }
        }
    }
}

@Composable
private fun DroneHeader(isOperator: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Orange),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Sensors,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                "Vue Drone",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                if (isOperator) "Contrôle et gestion du drone" else "Flux vidéo du drone",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun ScreenCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
private fun NoSessionCard(onCreateSession: () -> Unit) {
    ScreenCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Filled.Sensors,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(40.dp)
            )
            Text(
                "Aucune session drone active",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Button(
                onClick = onCreateSession,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Créer une session")
            }
        }
    }
}

@Composable
private fun VideoPanel(session: DroneSession, streamAvailable: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A202C), Color(0xFF0D1117))
                )
            )
    ) {
        if (streamAvailable) {
            // Mission name badge (top-left)
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    session.missionName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Pulsing LIVE pill (top-right)
            val transition = rememberInfiniteTransition(label = "live")
            val liveAlpha by transition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "liveAlpha"
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .alpha(liveAlpha)
                    .clip(RoundedCornerShape(50))
                    .background(AccentRed)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    "LIVE",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Crosshair at center
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .width(28.dp)
                            .height(1.5.dp)
                            .background(Color.White.copy(alpha = 0.9f))
                    )
                    Box(
                        Modifier
                            .width(1.5.dp)
                            .height(28.dp)
                            .background(Color.White.copy(alpha = 0.9f))
                    )
                }
                Text(
                    "Flux vidéo simulé",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }

            // Telemetry overlay (bottom-left)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp)
            ) {
                Text(
                    "ALT: ${session.altitude.toInt()}m",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "GPS: ${String.format(Locale.US, "%.4f", session.gpsLat)}, ${String.format(Locale.US, "%.4f", session.gpsLng)}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Capture button (bottom-right) — agreed stub, does nothing
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Capturer", fontSize = 12.sp)
            }
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Flux non disponible",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Le droniste n'a pas partagé le flux",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
        value()
    }
}

@Composable
private fun DroneStateCard(session: DroneSession) {
    ScreenCard {
        Text(
            "État du drone",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )

        InfoRow(label = "Statut") {
            StatusBadge(session.status)
        }

        val batteryColor = when {
            session.batteryLevel > 50 -> AccentGreen
            session.batteryLevel > 20 -> AccentAmber
            else -> AccentRed
        }
        InfoRow(label = "Batterie") {
            Text(
                "${session.batteryLevel}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = batteryColor
            )
        }

        InfoRow(label = "Altitude") {
            Text(
                "${session.altitude.toInt()} m",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
        }

        InfoRow(label = "Flux partagé") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (session.streamShared) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                    contentDescription = null,
                    tint = if (session.streamShared) AccentGreen else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    if (session.streamShared) "Oui" else "Non",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextDark
                )
            }
        }
    }
}

@Composable
private fun CommandsCard(session: DroneSession) {
    ScreenCard {
        Text(
            "Commandes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextDark
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (session.status) {
                DroneStatus.IDLE -> CommandButton("Lancer mission", Modifier.weight(1f)) {
                    DroneRepository.update {
                        it.copy(status = DroneStatus.FLYING, altitude = 25f)
                    }
                }
                DroneStatus.FLYING -> {
                    CommandButton("Pause", Modifier.weight(1f), outlined = true) {
                        DroneRepository.update { it.copy(status = DroneStatus.PAUSED) }
                    }
                    CommandButton("RTH", Modifier.weight(1f)) {
                        DroneRepository.update { it.copy(status = DroneStatus.RETURNING) }
                    }
                }
                DroneStatus.PAUSED -> {
                    CommandButton("Reprendre", Modifier.weight(1f), outlined = true) {
                        DroneRepository.update { it.copy(status = DroneStatus.FLYING) }
                    }
                    CommandButton("RTH", Modifier.weight(1f)) {
                        DroneRepository.update { it.copy(status = DroneStatus.RETURNING) }
                    }
                }
                DroneStatus.RETURNING -> CommandButton("Confirmer atterrissage", Modifier.weight(1f)) {
                    DroneRepository.update {
                        it.copy(status = DroneStatus.LANDED, altitude = 0f)
                    }
                }
                DroneStatus.LANDED -> CommandButton("Réinitialiser", Modifier.weight(1f)) {
                    DroneRepository.update { it.copy(status = DroneStatus.IDLE) }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Partager le flux",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
            Switch(
                checked = session.streamShared,
                onCheckedChange = { shared ->
                    DroneRepository.update { it.copy(streamShared = shared) }
                },
                colors = SwitchDefaults.colors(checkedTrackColor = Orange)
            )
        }
    }
}

@Composable
private fun CommandButton(
    label: String,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    onClick: () -> Unit
) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            border = BorderStroke(1.dp, BorderLight)
        ) {
            Text(label, color = TextDark)
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            colors = ButtonDefaults.buttonColors(containerColor = Orange)
        ) {
            Text(label)
        }
    }
}
