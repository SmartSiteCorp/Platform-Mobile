package com.smartsite.app.ui.screens.annotations

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsite.app.data.repository.Repositories
import com.smartsite.app.data.model.Annotation
import com.smartsite.app.data.model.AnnotationSupport
import com.smartsite.app.data.model.AnnotationType
import com.smartsite.app.data.model.Site
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import com.smartsite.app.util.frenchLabel

private val presetColors = listOf("#EF4444", "#D4A03A", "#56A87A", "#5B89C8", "#7C6DB5", "#C96585")

private fun parseHexColor(hex: String): Color =
    try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: IllegalArgumentException) {
        Color(android.graphics.Color.parseColor("#EF4444"))
    }

private fun AnnotationType.frenchLabel(): String = when (this) {
    AnnotationType.TEXT -> "Texte"
    AnnotationType.ARROW -> "Flèche"
    AnnotationType.CIRCLE -> "Cercle"
    AnnotationType.FREEHAND -> "Dessin libre"
}

private fun AnnotationType.icon(): ImageVector = when (this) {
    AnnotationType.TEXT -> Icons.Filled.TextFields
    AnnotationType.ARROW -> Icons.AutoMirrored.Filled.ArrowForward
    AnnotationType.CIRCLE -> Icons.Filled.RadioButtonUnchecked
    AnnotationType.FREEHAND -> Icons.Filled.Gesture
}

private fun AnnotationSupport.frenchLabel(): String = when (this) {
    AnnotationSupport.PLAN -> "Plan"
    AnnotationSupport.IMAGE -> "Image"
    AnnotationSupport.DRONE_CAPTURE -> "Capture drone"
}

@Composable
fun AnnotationsScreen() {
    val annotations by Repositories.annotations.annotations.collectAsState()
    val sites by Repositories.sites.sites.collectAsState()
    val user by Repositories.auth.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    val filtered = annotations.filter {
        it.content.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Cream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Annotations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    "${annotations.size} annotation(s)",
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
                Text("Nouvelle")
            }
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text("Rechercher une annotation…") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        if (filtered.isEmpty()) {
            EmptyState(
                icon = Icons.Filled.Gesture,
                title = "Aucune annotation",
                description = if (searchQuery.isBlank()) {
                    "Créez votre première annotation pour commencer"
                } else {
                    "Aucune annotation ne correspond à votre recherche"
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered, key = { it.id }) { annotation ->
                    AnnotationCard(annotation)
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateAnnotationDialog(
            sites = sites,
            onDismiss = { showCreateDialog = false },
            onConfirm = { site, type, support, content, zone, color ->
                Repositories.annotations.create(
                    type = type,
                    content = content,
                    support = support,
                    zone = zone,
                    color = color,
                    siteId = site.id,
                    authorRole = user.role
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
private fun AnnotationCard(annotation: Annotation) {
    val color = parseHexColor(annotation.color)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Row(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    annotation.type.icon(),
                    contentDescription = annotation.type.frenchLabel(),
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = annotation.content.ifBlank { annotation.type.frenchLabel() },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextDark
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlineBadge(annotation.type.frenchLabel())
                    OutlineBadge(annotation.support.frenchLabel())
                }
                if (annotation.zone.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Zone : ${annotation.zone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "${annotation.authorRole.frenchLabel()} · ${formatDate(annotation.createdDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun OutlineBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun CreateAnnotationDialog(
    sites: List<Site>,
    onDismiss: () -> Unit,
    onConfirm: (Site, AnnotationType, AnnotationSupport, String, String, String) -> Unit
) {
    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var selectedType by remember { mutableStateOf(AnnotationType.TEXT) }
    var selectedSupport by remember { mutableStateOf(AnnotationSupport.PLAN) }
    var content by remember { mutableStateOf("") }
    var zone by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#EF4444") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardWhite,
        title = {
            Text("Créer une annotation", fontWeight = FontWeight.Bold, color = TextDark)
        },
        text = {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormDropdown(
                    label = "Chantier*",
                    value = selectedSite?.name ?: "",
                    options = sites.map { it.id to it.name },
                    onSelect = { id -> selectedSite = sites.firstOrNull { it.id == id } }
                )
                FormDropdown(
                    label = "Type",
                    value = selectedType.frenchLabel(),
                    options = AnnotationType.entries.map { it.name to it.frenchLabel() },
                    onSelect = { name -> selectedType = AnnotationType.valueOf(name) }
                )
                FormDropdown(
                    label = "Support",
                    value = selectedSupport.frenchLabel(),
                    options = AnnotationSupport.entries.map { it.name to it.frenchLabel() },
                    onSelect = { name -> selectedSupport = AnnotationSupport.valueOf(name) }
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contenu") },
                    minLines = 2
                )
                OutlinedTextField(
                    value = zone,
                    onValueChange = { zone = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Zone") },
                    singleLine = true
                )
                Column {
                    Text(
                        "Couleur",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        presetColors.forEach { hex ->
                            val swatchColor = parseHexColor(hex)
                            val selected = hex == selectedColor
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .then(
                                        if (selected) {
                                            Modifier.border(3.dp, TextDark, CircleShape)
                                        } else {
                                            Modifier.border(1.dp, BorderLight, CircleShape)
                                        }
                                    )
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val site = selectedSite ?: return@Button
                    onConfirm(site, selectedType, selectedSupport, content.trim(), zone.trim(), selectedColor)
                },
                enabled = selectedSite != null,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextMuted)
            }
        }
    )
}

@Composable
private fun FormDropdown(
    label: String,
    value: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) }
        )
        Box(
            Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (key, optionLabel) ->
                DropdownMenuItem(
                    text = { Text(optionLabel) },
                    onClick = {
                        onSelect(key)
                        expanded = false
                    }
                )
            }
        }
    }
}
