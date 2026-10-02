package com.smartsite.app.ui.screens.sites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.smartsite.app.data.mock.SiteRepository
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SitesScreen(onSiteClick: (String) -> Unit) {
    val sites by SiteRepository.sites.collectAsState()
    var query by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf<SiteStatus?>(null) }
    var statusMenuOpen by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filtered = sites.filter { site ->
        (query.isBlank() ||
            site.name.contains(query, ignoreCase = true) ||
            site.address.contains(query, ignoreCase = true)) &&
            (statusFilter == null || site.status == statusFilter)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Chantiers",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    "${sites.size} chantier${if (sites.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Nouveau chantier")
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Rechercher un chantier...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite,
                focusedBorderColor = Orange,
                unfocusedBorderColor = BorderLight
            )
        )

        Spacer(Modifier.height(12.dp))

        Box {
            OutlinedButton(
                onClick = { statusMenuOpen = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = CardWhite,
                    contentColor = TextDark
                )
            ) {
                Text(statusFilter?.let { siteStatusLabel(it) } ?: "Tous")
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = TextMuted)
            }
            DropdownMenu(
                expanded = statusMenuOpen,
                onDismissRequest = { statusMenuOpen = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Tous") },
                    onClick = {
                        statusFilter = null
                        statusMenuOpen = false
                    }
                )
                SiteStatus.entries.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(siteStatusLabel(status)) },
                        onClick = {
                            statusFilter = status
                            statusMenuOpen = false
                        }
                    )
                }
            }
        }

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
        CreateSiteDialog(onDismiss = { showCreateDialog = false })
    }
}

private fun siteStatusLabel(status: SiteStatus): String = when (status) {
    SiteStatus.PLANNING -> "Planification"
    SiteStatus.IN_PROGRESS -> "En cours"
    SiteStatus.COMPLETED -> "Terminé"
    SiteStatus.ON_HOLD -> "En pause"
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
        Column {
            val imageModifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            if (site.imageRes != null) {
                LocalImage(
                    res = site.imageRes,
                    contentDescription = site.name,
                    modifier = imageModifier
                )
            } else {
                Box(
                    modifier = imageModifier.background(
                        Brush.linearGradient(listOf(Sage, SageDark))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.Domain,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
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
                        color = TextDark,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    StatusBadge(site.status)
                }
                Spacer(Modifier.height(8.dp))
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
                        "Début : ${formatDate(site.startDate)}",
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
private fun CreateSiteDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now().plusMonths(6)) }
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = {
            Text("Créer un chantier", fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nom*") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Adresse") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Description") },
                    minLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth()) {
                    DateField(
                        label = "Date début",
                        date = startDate,
                        onClick = { showStartPicker = true },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    DateField(
                        label = "Date fin",
                        date = endDate,
                        onClick = { showEndPicker = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    SiteRepository.create(
                        name = name.trim(),
                        address = address.trim(),
                        description = description.trim(),
                        startDate = startDate,
                        endDate = endDate
                    )
                    onDismiss()
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange,
                    contentColor = Color.White
                )
            ) {
                Text("Créer le chantier")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextMuted)
            }
        }
    )

    if (showStartPicker) {
        SiteDatePickerDialog(
            initial = startDate,
            onDismiss = { showStartPicker = false },
            onConfirm = {
                startDate = it
                showStartPicker = false
            }
        )
    }
    if (showEndPicker) {
        SiteDatePickerDialog(
            initial = endDate,
            onDismiss = { showEndPicker = false },
            onConfirm = {
                endDate = it
                showEndPicker = false
            }
        )
    }
}

@Composable
private fun DateField(
    label: String,
    date: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMuted)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderLight),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDark)
        ) {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(formatDate(date))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SiteDatePickerDialog(
    initial: LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initial.toPickerMillis())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { onConfirm(it.pickerMillisToLocalDate()) }
                        ?: onDismiss()
                }
            ) {
                Text("OK", color = Orange)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextMuted)
            }
        }
    ) {
        DatePicker(state = state)
    }
}
