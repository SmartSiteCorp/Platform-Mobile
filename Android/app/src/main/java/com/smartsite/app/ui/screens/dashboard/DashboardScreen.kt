package com.smartsite.app.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.mock.MockAuthRepository
import com.smartsite.app.data.mock.SiteRepository
import com.smartsite.app.data.mock.TaskRepository
import com.smartsite.app.data.model.Site
import com.smartsite.app.data.model.SiteStatus
import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.components.PriorityBadge
import com.smartsite.app.ui.components.StatCard
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentOrange
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.AccentSage
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate

@Composable
fun DashboardScreen(
    onTaskClick: (String) -> Unit,
    onSiteClick: (String) -> Unit,
    onSeeAllTasks: () -> Unit,
    onSeeAllSites: () -> Unit
) {
    val user by MockAuthRepository.currentUser.collectAsState()
    val tasks by TaskRepository.tasks.collectAsState()
    val sites by SiteRepository.sites.collectAsState()

    val firstName = user.fullName.substringBefore(" ")
    val activeSites = sites.count { it.status == SiteStatus.IN_PROGRESS }
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val blockedTasks = tasks.count { it.status == TaskStatus.BLOCKED }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
    ) {
        // Header
        Text(
            text = buildAnnotatedString {
                append("Bonjour ")
                withStyle(SpanStyle(color = Orange)) { append(firstName) }
                append(" 👋")
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Vue d'ensemble de vos chantiers",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )

        Spacer(Modifier.height(20.dp))

        // Stats — 2-column grid
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Chantiers actifs",
                value = activeSites.toString(),
                icon = Icons.Filled.Domain,
                color = AccentSage,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Tâches totales",
                value = tasks.size.toString(),
                icon = Icons.AutoMirrored.Filled.Assignment,
                color = AccentOrange,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(
                title = "Terminées",
                value = completedTasks.toString(),
                icon = Icons.Filled.CheckCircle,
                color = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Bloquées",
                value = blockedTasks.toString(),
                icon = Icons.Filled.Warning,
                color = AccentRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Recent tasks
        SectionHeader(title = "Tâches récentes", onSeeAll = onSeeAllTasks)
        Spacer(Modifier.height(8.dp))
        val recentTasks = tasks.take(5)
        if (recentTasks.isEmpty()) {
            EmptyState(
                icon = Icons.AutoMirrored.Filled.Assignment,
                title = "Aucune tâche pour le moment",
                description = "Les tâches créées sur vos chantiers apparaîtront ici"
            )
        } else {
            recentTasks.forEach { task ->
                TaskCard(task = task, onClick = { onTaskClick(task.id) })
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Sites
        SectionHeader(title = "Chantiers", onSeeAll = onSeeAllSites)
        Spacer(Modifier.height(8.dp))
        val recentSites = sites.take(5)
        if (recentSites.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Domain,
                title = "Aucun chantier",
                description = "Créez votre premier chantier pour commencer"
            )
        } else {
            recentSites.forEach { site ->
                SiteCard(site = site, onClick = { onSiteClick(site.id) })
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onSeeAll) {
            Text("Tout voir →", color = Orange, fontWeight = FontWeight.SemiBold)
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
                text = task.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(task.status)
                Spacer(Modifier.width(6.dp))
                PriorityBadge(task.priority)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatDate(task.dueDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SiteCard(site: Site, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = site.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatusBadge(site.status)
        }
    }
}
