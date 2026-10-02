package com.smartsite.app.ui.screens.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.smartsite.app.data.mock.MediaRepository
import com.smartsite.app.data.mock.SiteRepository
import com.smartsite.app.data.model.Media
import com.smartsite.app.data.model.MediaType
import com.smartsite.app.data.model.Site
import com.smartsite.app.ui.components.EmptyState
import com.smartsite.app.ui.components.LocalImage
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import com.smartsite.app.util.frenchLabel
import com.smartsite.app.util.icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaGalleryScreen() {
    val mediaList by MediaRepository.media.collectAsState()
    val sites by SiteRepository.sites.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<MediaType?>(null) }
    var detailMedia by remember { mutableStateOf<Media?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }

    val filtered = mediaList.filter { media ->
        (selectedType == null || media.type == selectedType) &&
            media.caption.contains(searchQuery, ignoreCase = true)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Médiathèque",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${mediaList.size} fichier(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Button(
                    onClick = { showUploadDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Orange,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        Icons.Filled.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Uploader")
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("Rechercher…") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = TextMuted)
                    }
                )
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedType?.frenchLabel() ?: "Tous types",
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(typeExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tous types") },
                            onClick = {
                                selectedType = null
                                typeExpanded = false
                            }
                        )
                        MediaType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.frenchLabel()) },
                                onClick = {
                                    selectedType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EmptyState(
                    icon = Icons.Filled.PhotoLibrary,
                    title = "Aucun média",
                    description = "Uploadez vos premiers fichiers"
                )
            }
        } else {
            items(filtered, key = { it.id }) { media ->
                MediaCard(media = media, onClick = { detailMedia = media })
            }
        }
    }

    detailMedia?.let { media ->
        MediaDetailDialog(media = media, onDismiss = { detailMedia = null })
    }

    if (showUploadDialog) {
        UploadMediaDialog(
            sites = sites,
            onDismiss = { showUploadDialog = false }
        )
    }
}

@Composable
private fun MediaCard(media: Media, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Box(Modifier.fillMaxSize()) {
            LocalImage(
                res = media.fileRes,
                uri = media.fileUri,
                contentDescription = media.caption,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    media.type.icon(),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    media.type.frenchLabel(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
                    .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 8.dp)
            ) {
                Text(
                    media.caption.ifBlank { media.type.frenchLabel() },
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MediaDetailDialog(media: Media, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BorderLight),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column {
                LocalImage(
                    res = media.fileRes,
                    uri = media.fileUri,
                    contentDescription = media.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                Column(Modifier.padding(16.dp)) {
                    Text(
                        media.caption.ifBlank { media.type.frenchLabel() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .background(Cream, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            media.type.icon(),
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            media.type.frenchLabel(),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Ajouté le ${formatDate(media.createdDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(Modifier.height(16.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Fermer")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadMediaDialog(sites: List<Site>, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var pickedName by remember { mutableStateOf<String?>(null) }
    var selectedSite by remember { mutableStateOf<Site?>(null) }
    var uploadType by remember { mutableStateOf(MediaType.PHOTO) }
    var caption by remember { mutableStateOf("") }

    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            pickedUri = uri
            pickedName = displayName(context, uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un média") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { pickerLauncher.launch(arrayOf("image/*", "application/pdf")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Filled.Upload,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        pickedName ?: "Choisir un fichier",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                var siteExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = siteExpanded,
                    onExpandedChange = { siteExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSite?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chantier*") },
                        placeholder = { Text("Sélectionner un chantier") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(siteExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = siteExpanded,
                        onDismissRequest = { siteExpanded = false }
                    ) {
                        sites.forEach { site ->
                            DropdownMenuItem(
                                text = { Text(site.name) },
                                onClick = {
                                    selectedSite = site
                                    siteExpanded = false
                                }
                            )
                        }
                    }
                }

                var uploadTypeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = uploadTypeExpanded,
                    onExpandedChange = { uploadTypeExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uploadType.frenchLabel(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(uploadTypeExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = uploadTypeExpanded,
                        onDismissRequest = { uploadTypeExpanded = false }
                    ) {
                        MediaType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.frenchLabel()) },
                                onClick = {
                                    uploadType = type
                                    uploadTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Légende") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val uri = pickedUri
                    val site = selectedSite
                    if (uri != null && site != null) {
                        MediaRepository.create(
                            fileUri = uri.toString(),
                            type = uploadType,
                            caption = caption.trim(),
                            siteId = site.id
                        )
                    }
                    onDismiss()
                },
                enabled = pickedUri != null && selectedSite != null,
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

private fun displayName(context: Context, uri: Uri): String {
    val fromProvider = runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
        }
    }.getOrNull()
    return fromProvider ?: uri.lastPathSegment ?: "fichier"
}
