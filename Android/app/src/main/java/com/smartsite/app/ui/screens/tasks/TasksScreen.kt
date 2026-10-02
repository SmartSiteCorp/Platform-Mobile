package com.smartsite.app.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.mock.MockAuthRepository
import com.smartsite.app.data.mock.SiteRepository
import com.smartsite.app.data.mock.TaskRepository
import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskPriority
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.data.model.UserRole
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.components.PriorityBadge
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import com.smartsite.app.util.pickerMillisToLocalDate
import com.smartsite.app.util.toPickerMillis
import java.time.LocalDate

private fun TaskStatus.frenchLabel(): String = when (this) {
    TaskStatus.PENDING -> "En attente"
    TaskStatus.IN_PROGRESS -> "En cours"
    TaskStatus.COMPLETED -> "Terminé"
    TaskStatus.BLOCKED -> "Bloqué"
}

private fun TaskPriority.frenchLabel(): String = when (this) {
    TaskPriority.LOW -> "Faible"
    TaskPriority.MEDIUM -> "Moyen"
    TaskPriority.HIGH -> "Élevé"
    TaskPriority.URGENT -> "Urgent"
}

@Composable
fun TasksScreen(onTaskClick: (String) -> Unit) {
    val user by MockAuthRepository.currentUser.collectAsState()
    val tasks by TaskRepository.tasks.collectAsState()
    val sites by SiteRepository.sites.collectAsState()

    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<TaskStatus?>(null) }
    var priorityFilter by remember { mutableStateOf<TaskPriority?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val siteNames = sites.associate { it.id to it.name }

    val filtered = tasks.filter { task ->
        (query.isBlank() || task.title.contains(query, ignoreCase = true)) &&
            (statusFilter == null || task.status == statusFilter) &&
            (priorityFilter == null || task.priority == priorityFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = if (user.role == UserRole.WORKER) "Mes tâches" else "Tâches",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "${filtered.size} tâche(s)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            if (user.role != UserRole.WORKER) {
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nouvelle tâche")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher une tâche…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterDropdown(
                options = listOf<Pair<TaskStatus?, String>>(null to "Tous") +
                    TaskStatus.entries.map { it to it.frenchLabel() },
                selected = statusFilter,
                onSelected = { statusFilter = it },
                modifier = Modifier.weight(1f)
            )
            FilterDropdown(
                options = listOf<Pair<TaskPriority?, String>>(null to "Toutes") +
                    TaskPriority.entries.map { it to it.frenchLabel() },
                selected = priorityFilter,
                onSelected = { priorityFilter = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.CheckCircle,
                title = "Aucune tâche trouvée",
                description = "Aucune tâche ne correspond à vos critères de recherche."
            )
        } else {
            filtered.forEach { task ->
                TaskCard(
                    task = task,
                    siteName = siteNames[task.siteId],
                    onClick = { onTaskClick(task.id) }
                )
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showCreateDialog) {
        CreateTaskDialog(onDismiss = { showCreateDialog = false })
    }
}

@Composable
private fun TaskCard(task: Task, siteName: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                if (siteName != null) {
                    Text(
                        text = siteName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(task.status)
            Spacer(Modifier.width(8.dp))
            PriorityBadge(task.priority)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Zone : ${task.zone}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Schedule,
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

/** Small white dropdown button used for the filters and the form fields. */
@Composable
private fun <T> FilterDropdown(
    options: List<Pair<T?, String>>,
    selected: T?,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.first == selected }?.second ?: label ?: ""
    Box(modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = CardWhite,
                contentColor = if (selected == null && label != null) TextMuted else TextDark
            )
        ) {
            Text(
                text = selectedLabel,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTaskDialog(onDismiss: () -> Unit) {
    val sites by SiteRepository.sites.collectAsState()

    var title by remember { mutableStateOf("") }
    var selectedSiteId by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf<TaskPriority?>(TaskPriority.MEDIUM) }
    var zone by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val canConfirm = title.isNotBlank() && selectedSiteId != null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer une tâche", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Titre *") },
                    singleLine = true
                )
                FilterDropdown(
                    options = sites.map { it.id to it.name },
                    selected = selectedSiteId,
                    onSelected = { selectedSiteId = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Chantier *"
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    minLines = 2
                )
                FilterDropdown(
                    options = TaskPriority.entries.map { it to it.frenchLabel() },
                    selected = priority,
                    onSelected = { priority = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Priorité"
                )
                OutlinedTextField(
                    value = zone,
                    onValueChange = { zone = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Zone") },
                    placeholder = { Text("ex: Bâtiment A") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = assignedTo,
                    onValueChange = { assignedTo = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Assigné à (email)") },
                    singleLine = true
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = dueDate?.let { "Date limite : ${formatDate(it)}" } ?: "Date limite",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val siteId = selectedSiteId ?: return@Button
                    TaskRepository.create(
                        title = title.trim(),
                        siteId = siteId,
                        description = description.trim(),
                        priority = priority ?: TaskPriority.MEDIUM,
                        zone = zone.trim(),
                        assignedTo = assignedTo.trim(),
                        dueDate = dueDate ?: LocalDate.now()
                    )
                    onDismiss()
                },
                enabled = canConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Créer la tâche")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = (dueDate ?: LocalDate.now()).toPickerMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { dueDate = it.pickerMillisToLocalDate() }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
