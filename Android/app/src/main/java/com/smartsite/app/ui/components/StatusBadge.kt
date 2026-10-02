package com.smartsite.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsite.app.data.model.DroneStatus
import com.smartsite.app.data.model.SiteStatus
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.ui.theme.AccentAmber
import com.smartsite.app.ui.theme.AccentBlue
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentOrange
import com.smartsite.app.ui.theme.AccentPurple
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.SmartSiteTheme
import com.smartsite.app.ui.theme.TextMuted

private val Slate = Color(0xFF64748B)

/** Small outlined pill with a French status label. Mirrors `StatusBadge.jsx`. */
@Composable
private fun BadgePill(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun StatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    when (status) {
        TaskStatus.PENDING -> BadgePill("En attente", Slate, modifier)
        TaskStatus.IN_PROGRESS -> BadgePill("En cours", AccentBlue, modifier)
        TaskStatus.COMPLETED -> BadgePill("Terminé", AccentGreen, modifier)
        TaskStatus.BLOCKED -> BadgePill("Bloqué", AccentRed, modifier)
    }
}

@Composable
fun StatusBadge(status: SiteStatus, modifier: Modifier = Modifier) {
    when (status) {
        SiteStatus.PLANNING -> BadgePill("Planification", AccentPurple, modifier)
        SiteStatus.IN_PROGRESS -> BadgePill("En cours", AccentBlue, modifier)
        SiteStatus.COMPLETED -> BadgePill("Terminé", AccentGreen, modifier)
        SiteStatus.ON_HOLD -> BadgePill("En pause", AccentAmber, modifier)
    }
}

@Composable
fun StatusBadge(status: DroneStatus, modifier: Modifier = Modifier) {
    when (status) {
        DroneStatus.IDLE -> BadgePill("Inactif", Slate, modifier)
        DroneStatus.FLYING -> BadgePill("En vol", AccentBlue, modifier)
        DroneStatus.PAUSED -> BadgePill("En pause", AccentAmber, modifier)
        DroneStatus.RETURNING -> BadgePill("Retour", AccentOrange, modifier)
        DroneStatus.LANDED -> BadgePill("Posé", AccentGreen, modifier)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusBadgePreview() {
    SmartSiteTheme {
        androidx.compose.foundation.layout.Row {
            StatusBadge(TaskStatus.PENDING)
            StatusBadge(TaskStatus.IN_PROGRESS, Modifier.padding(start = 4.dp))
            StatusBadge(TaskStatus.COMPLETED, Modifier.padding(start = 4.dp))
            StatusBadge(TaskStatus.BLOCKED, Modifier.padding(start = 4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SiteStatusBadgePreview() {
    SmartSiteTheme {
        androidx.compose.foundation.layout.Row {
            StatusBadge(SiteStatus.PLANNING)
            StatusBadge(SiteStatus.ON_HOLD, Modifier.padding(start = 4.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DroneStatusBadgePreview() {
    SmartSiteTheme {
        androidx.compose.foundation.layout.Row {
            StatusBadge(DroneStatus.IDLE)
            StatusBadge(DroneStatus.FLYING, Modifier.padding(start = 4.dp))
            StatusBadge(DroneStatus.RETURNING, Modifier.padding(start = 4.dp))
        }
    }
}
