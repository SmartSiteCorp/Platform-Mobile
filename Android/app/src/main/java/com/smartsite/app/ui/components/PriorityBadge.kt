package com.smartsite.app.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsite.app.data.model.TaskPriority
import com.smartsite.app.ui.theme.AccentBlue
import com.smartsite.app.ui.theme.AccentOrange
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.SmartSiteTheme

private val Slate = Color(0xFF64748B)

/** Small outlined pill with a French priority label. Mirrors `PriorityBadge.jsx`. */
@Composable
fun PriorityBadge(priority: TaskPriority, modifier: Modifier = Modifier) {
    val (label, color) = when (priority) {
        TaskPriority.LOW -> "Faible" to Slate
        TaskPriority.MEDIUM -> "Moyen" to AccentBlue
        TaskPriority.HIGH -> "Élevé" to AccentOrange
        TaskPriority.URGENT -> "Urgent" to AccentRed
    }
    Text(
        text = label,
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 3.dp),
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview(showBackground = true)
@Composable
private fun PriorityBadgePreview() {
    SmartSiteTheme {
        Row {
            PriorityBadge(TaskPriority.LOW)
            PriorityBadge(TaskPriority.MEDIUM, Modifier.padding(start = 4.dp))
            PriorityBadge(TaskPriority.HIGH, Modifier.padding(start = 4.dp))
            PriorityBadge(TaskPriority.URGENT, Modifier.padding(start = 4.dp))
        }
    }
}
