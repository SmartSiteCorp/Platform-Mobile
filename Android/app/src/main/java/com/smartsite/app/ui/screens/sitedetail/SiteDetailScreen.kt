package com.smartsite.app.ui.screens.sitedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.model.Site
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.data.repository.Repositories
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.components.LocalImage
import com.smartsite.app.ui.components.PriorityBadge
import com.smartsite.app.ui.components.StatCard
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.AccentBlue
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentPurple
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import com.smartsite.app.util.icon

@Composable
fun SiteDetailScreen(
    siteId: String,
    onBack: () -> Unit,
    onTaskClick: (String) -> Unit
) {
    val sites by Repositories.sites.sites.collectAsState()
    val tasks by Repositories.tasks.tasks.collectAsState()
    val media by Repositories.media.media.collectAsState()

    val site = sites.firstOrNull { it.id == siteId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        TextButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Orange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Retour aux chantiers", color = Orange)
        }

        if (site == null) {
            Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Chantier introuvable", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) {
                        Text("Retour aux chantiers")
                    }
                }
            }
            return@Column
        }

        SiteHeader(site)

        Spacer(Modifier.height(16.dp))

        val siteTasks = tasks.filter { it.siteId == site.id }
        val siteMedia = media.filter { it.siteId == site.id }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Total tâches",
                value = siteTasks.size.toString(),
                icon = Icons.Filled.Assignment,
                color = AccentPurple,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "En cours",
                value = siteTasks.count { it.status == TaskStatus.IN_PROGRESS }.toString(),
                icon = Icons.Filled.Assignment,
                color = AccentBlue,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Terminées",
                value = siteTasks.count { it.status == TaskStatus.COMPLETED }.toString(),
                icon = Icons.Filled.Assignment,
                color = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Bloquées",
                value = siteTasks.count { it.status == TaskStatus.BLOCKED }.toString(),
                icon = Icons.Filled.Assignment,
                color = AccentRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        var selectedTab by remember { mutableIntStateOf(0) }
        TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Tâches (${siteTasks.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Médias (${siteMedia.size})") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Plans") }
            )
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                if (siteTasks.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Assignment,
                        title = "Aucune tâche",
                        description = "Ce chantier n'a pas encore de tâche"
                    )
                } else {
                    siteTasks.forEach { task ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTaskClick(task.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    task.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusBadge(task.status)
                                    PriorityBadge(task.priority)
                                    Text(
                                        "Zone : ${task.zone}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            1 -> {
                if (siteMedia.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Image,
                        title = "Aucun média",
                        description = "Ce chantier n'a pas encore de média"
                    )
                } else {
                    siteMedia.chunked(2).forEach { rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowItems.forEach { item ->
                                MediaTile(
                                    item = item,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
            else -> {
                if (site.planRes.isEmpty()) {
                    EmptyState(
                        icon = Icons.Filled.Image,
                        title = "Aucun plan disponible",
                        description = "Ce chantier n'a pas encore de plan"
                    )
                } else {
                    site.planRes.chunked(2).forEachIndexed { rowIndex, rowItems ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowItems.forEachIndexed { index, planRes ->
                                Column(modifier = Modifier.weight(1f)) {
                                    LocalImage(
                                        res = planRes,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Plan ${rowIndex * 2 + index + 1}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted
                                    )
                                }
                            }
                            if (rowItems.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SiteHeader(site: Site) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AccentBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Domain, contentDescription = null, tint = Color.White)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        site.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    StatusBadge(site.status)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(site.address, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "${formatDate(site.startDate)} — ${formatDate(site.endDate)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            if (site.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(site.description, style = MaterialTheme.typography.bodyMedium, color = TextDark)
            }
        }
    }
}

@Composable
private fun MediaTile(item: com.smartsite.app.data.model.Media, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
    ) {
        LocalImage(
            res = item.fileRes,
            uri = item.fileUri,
            contentDescription = item.caption,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    item.type.icon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    item.caption.ifBlank { "Média" },
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
    }
}
