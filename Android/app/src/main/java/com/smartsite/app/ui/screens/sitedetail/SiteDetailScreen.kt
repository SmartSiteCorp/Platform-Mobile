package com.smartsite.app.ui.screens.sitedetail

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.model.Media
import com.smartsite.app.data.model.Site
import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.data.mock.MediaRepository
import com.smartsite.app.data.mock.SiteRepository
import com.smartsite.app.data.mock.TaskRepository
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
import com.smartsite.app.util.frenchLabel

@Composable
fun SiteDetailScreen(
    siteId: String,
    onBack: () -> Unit,
    onTaskClick: (String) -> Unit
) {
    val sites by SiteRepository.sites.collectAsState()
    val tasks by TaskRepository.tasks.collectAsState()
    val media by MediaRepository.media.collectAsState()
    val site = sites.firstOrNull { it.id == siteId }

    if (site == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Cream)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Chantier introuvable",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Text("Retour aux chantiers")
            }
        }
        return
    }

    val siteTasks = tasks.filter { it.siteId == site.id }
    val siteMedia = media.filter { it.siteId == site.id }
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
    ) {
        Text(
            "← Retour aux chantiers",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Orange,
            modifier = Modifier.clickable(onClick = onBack)
        )

        Spacer(Modifier.height(12.dp))

        SiteHeaderCard(site)

        Spacer(Modifier.height(16.dp))

        val total = siteTasks.size
        val inProgress = siteTasks.count { it.status == TaskStatus.IN_PROGRESS }
        val completed = siteTasks.count { it.status == TaskStatus.COMPLETED }
        val blocked = siteTasks.count { it.status == TaskStatus.BLOCKED }

        Row(Modifier.fillMaxWidth()) {
            StatCard(
                title = "Total tâches",
                value = "$total",
                icon = Icons.Filled.Assignment,
                color = AccentPurple,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            StatCard(
                title = "En cours",
                value = "$inProgress",
                icon = Icons.Filled.Schedule,
                color = AccentBlue,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            StatCard(
                title = "Terminées",
                value = "$completed",
                icon = Icons.Filled.CheckCircle,
                color = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            StatCard(
                title = "Bloquées",
                value = "$blocked",
                icon = Icons.Filled.Block,
                color = AccentRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Cream,
            contentColor = Orange
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Tâches (${siteTasks.size})") },
                selectedContentColor = Orange,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Médias (${siteMedia.size})") },
                selectedContentColor = Orange,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Plans") },
                selectedContentColor = Orange,
                unselectedContentColor = TextMuted
            )
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> TasksTab(siteTasks, onTaskClick)
            1 -> MediaTab(siteMedia)
            else -> PlansTab(site.planRes)
        }
    }
}

@Composable
private fun SiteHeaderCard(site: Site) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderLight)
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
                    Icon(
                        Icons.Filled.Domain,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        site.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(4.dp))
                    StatusBadge(site.status)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(site.address, style = MaterialTheme.typography.bodySmall, color = TextMuted)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${formatDate(site.startDate)} — ${formatDate(site.endDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            if (site.description.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    site.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun TasksTab(tasks: List<Task>, onTaskClick: (String) -> Unit) {
    if (tasks.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Assignment,
            title = "Aucune tâche",
            description = "Les tâches de ce chantier apparaîtront ici"
        )
    } else {
        tasks.forEach { task ->
            TaskCard(task = task, onClick = { onTaskClick(task.id) })
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TaskCard(task: Task, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(task.status)
                Spacer(Modifier.width(8.dp))
                PriorityBadge(task.priority)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Zone : ${task.zone}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun MediaTab(media: List<Media>) {
    if (media.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.PhotoCamera,
            title = "Aucun média",
            description = "Les photos et captures de ce chantier apparaîtront ici"
        )
    } else {
        media.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth()) {
                rowItems.forEach { item ->
                    MediaCell(
                        image = {
                            LocalImage(
                                res = item.fileRes,
                                uri = item.fileUri,
                                contentDescription = item.caption,
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        title = item.caption,
                        subtitle = item.type.frenchLabel(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun PlansTab(planRes: List<Int>) {
    if (planRes.isEmpty()) {
        EmptyState(
            icon = Icons.Filled.Image,
            title = "Aucun plan disponible",
            description = "Les plans de ce chantier apparaîtront ici"
        )
    } else {
        planRes.mapIndexed { index, res -> index to res }.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth()) {
                rowItems.forEach { (index, res) ->
                    MediaCell(
                        image = {
                            LocalImage(
                                res = res,
                                contentDescription = "Plan ${index + 1}",
                                modifier = Modifier.fillMaxSize()
                            )
                        },
                        title = "Plan ${index + 1}",
                        subtitle = null,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                }
                if (rowItems.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MediaCell(
    image: @Composable () -> Unit,
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
    ) {
        image()
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                    )
                )
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
