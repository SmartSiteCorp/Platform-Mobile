package com.smartsite.app.ui.screens.sites

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.model.Site
import com.smartsite.app.data.model.SiteStatus
import com.smartsite.app.data.repository.Repositories
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.components.LocalImage
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.Sage
import com.smartsite.app.ui.theme.SageDark
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import com.smartsite.app.util.pickerMillisToLocalDate
import com.smartsite.app.util.toPickerMillis
import java.time.LocalDate

@Composable
fun SitesScreen(onSiteClick: (String) -> Unit) {
    val sites by Repositories.sites.sites.collectAsState()
    var search by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<SiteStatus?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filtered = sites.filter { site ->
        (search.isBlank() ||
            site.name.contains(search, ignoreCase = true) ||
            site.address.contains(search, ignoreCase = true)) &&
            (statusFilter == null || site.status == statusFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Chantiers", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${filtered.size} chantier(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMuted
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Nouveau chantier")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher un chantier...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        StatusFilterDropdown(selected = statusFilter, onSelect = { statusFilter = it })

        Spacer(Modifier.height(16.dp))

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Domain,
                title = "Aucun chantier trouvé",
                description = "Créez votre premier chantier pour commencer"
            )
        } else {
            filtered.forEach { site ->
                SiteCard(site = site, onClick = { onSiteClick(site.id) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (showCreateDialog) {
        CreateSiteDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, address, description, start, end ->
                Repositories.sites.create(name, address, description, start, end)
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun StatusFilterDropdown(
    selected: SiteStatus?,
    onSelect: (SiteStatus?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options: List<Pair<SiteStatus?, String>> = listOf(
        null to "Tous",
        SiteStatus.PLANNING to "Planification",
        SiteStatus.IN_PROGRESS to "En cours",
        SiteStatus.COMPLETED to "Terminé",
        SiteStatus.ON_HOLD to "En pause"
    )
    val label = options.first { it.first == selected }.second

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(label, color = TextDark)
            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = TextMuted)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (status, optionLabel) ->
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    onClick = {
                        onSelect(status)
                        expanded = false
                    }
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
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Column {
            if (site.imageRes != null) {
                LocalImage(
                    res = site.imageRes,
                    contentDescription = site.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.2f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.2f)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .background(Brush.linearGradient(listOf(Sage, SageDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Domain,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            Column(Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        site.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(site.status)
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(site.address, style = MaterialTheme.typography.bodySmall, color = TextMuted)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.DateRange, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        formatDate(site.startDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateSiteDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, address: String, description: String, start: LocalDate, end: LocalDate) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(6)) }
    var pickingStart by remember { mutableStateOf(false) }
    var pickingEnd by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer un chantier") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { pickingStart = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Début : ${formatDate(startDate)}", color = TextDark, style = MaterialTheme.typography.bodySmall)
                    }
                    OutlinedButton(
                        onClick = { pickingEnd = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fin : ${formatDate(endDate)}", color = TextDark, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name.trim(), address.trim(), description.trim(), startDate, endDate) },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Créer le chantier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )

    if (pickingStart) {
        val state = rememberDatePickerState(initialSelectedDateMillis = startDate.toPickerMillis())
        DatePickerDialog(
            onDismissRequest = { pickingStart = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { startDate = it.pickerMillisToLocalDate() }
                    pickingStart = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { pickingStart = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (pickingEnd) {
        val state = rememberDatePickerState(initialSelectedDateMillis = endDate.toPickerMillis())
        DatePickerDialog(
            onDismissRequest = { pickingEnd = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { endDate = it.pickerMillisToLocalDate() }
                    pickingEnd = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { pickingEnd = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}
