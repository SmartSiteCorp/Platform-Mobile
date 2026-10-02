package com.smartsite.app.ui.screens.taskdetail

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import com.smartsite.app.data.mock.MockAuthRepository
import com.smartsite.app.data.mock.SiteRepository
import com.smartsite.app.data.mock.TaskRepository
import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskStatus
import com.smartsite.app.ui.components.LocalImage
import com.smartsite.app.ui.components.PriorityBadge
import com.smartsite.app.ui.components.StatusBadge
import com.smartsite.app.ui.theme.AccentBlue
import com.smartsite.app.ui.theme.AccentGreen
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.BorderLight
import com.smartsite.app.ui.theme.CardWhite
import com.smartsite.app.ui.theme.Cream
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.formatDate
import java.io.File
import java.io.FileOutputStream

@Composable
fun TaskDetailScreen(
    taskId: String,
    onBack: () -> Unit
) {
    val tasks by TaskRepository.tasks.collectAsState()
    val sites by SiteRepository.sites.collectAsState()
    val user by MockAuthRepository.currentUser.collectAsState()
    val task = tasks.firstOrNull { it.id == taskId }

    var showValidationDialog by remember { mutableStateOf(false) }
    var showBlockingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("Retour aux tâches")
        }

        Spacer(Modifier.height(8.dp))

        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Tâche introuvable",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(onClick = onBack) { Text("Retour aux tâches") }
                }
            }
        } else {
            val siteName = sites.firstOrNull { it.id == task.siteId }?.name

            TaskHeaderCard(task = task, siteName = siteName)

            Spacer(Modifier.height(16.dp))

            if (task.assignedTo == user.email && task.status != TaskStatus.COMPLETED) {
                when (task.status) {
                    TaskStatus.PENDING -> {
                        Button(
                            onClick = {
                                TaskRepository.update(task.copy(status = TaskStatus.IN_PROGRESS))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text("Démarrer la tâche")
                        }
                        Spacer(Modifier.height(8.dp))
                        ValidateAndBlockRow(
                            onValidate = { showValidationDialog = true },
                            onBlock = { showBlockingDialog = true }
                        )
                    }
                    TaskStatus.IN_PROGRESS -> {
                        ValidateAndBlockRow(
                            onValidate = { showValidationDialog = true },
                            onBlock = { showBlockingDialog = true }
                        )
                    }
                    TaskStatus.BLOCKED -> Unit
                    TaskStatus.COMPLETED -> Unit
                }
                Spacer(Modifier.height(16.dp))
            }

            if (task.status == TaskStatus.COMPLETED) {
                CompletionCard(task)
                Spacer(Modifier.height(16.dp))
            }

            if (task.status == TaskStatus.BLOCKED) {
                BlockingCard(task)
                Spacer(Modifier.height(16.dp))
            }
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showValidationDialog && task != null) {
        PhotoReportDialog(
            title = "Valider la tâche",
            fieldLabel = "Commentaire *",
            photoRequired = true,
            confirmLabel = "Valider",
            confirmColor = AccentGreen,
            onDismiss = { showValidationDialog = false },
            onConfirm = { comment, photoUri ->
                TaskRepository.update(
                    task.copy(
                        status = TaskStatus.COMPLETED,
                        completionComment = comment,
                        completionPhotoUri = photoUri
                    )
                )
                showValidationDialog = false
            }
        )
    }

    if (showBlockingDialog && task != null) {
        PhotoReportDialog(
            title = "Signaler un blocage",
            fieldLabel = "Raison *",
            photoRequired = false,
            confirmLabel = "Signaler",
            confirmColor = AccentRed,
            onDismiss = { showBlockingDialog = false },
            onConfirm = { reason, photoUri ->
                TaskRepository.update(
                    task.copy(
                        status = TaskStatus.BLOCKED,
                        blockingReason = reason,
                        blockingPhotoUri = photoUri
                    )
                )
                showBlockingDialog = false
            }
        )
    }
}

@Composable
private fun TaskHeaderCard(task: Task, siteName: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardWhite)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = task.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        if (siteName != null) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Place,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(siteName, style = MaterialTheme.typography.bodyMedium, color = TextMuted)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusBadge(task.status)
            Spacer(Modifier.width(8.dp))
            PriorityBadge(task.priority)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Zone : ${task.zone}",
                modifier = Modifier
                    .border(1.dp, TextMuted.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Schedule,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Échéance : ${formatDate(task.dueDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Assigné à : ${task.assignedTo}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
        if (task.description.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark
            )
        }
    }
}

@Composable
private fun ValidateAndBlockRow(onValidate: () -> Unit, onBlock: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onValidate,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text("Valider")
        }
        OutlinedButton(
            onClick = onBlock,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, AccentRed),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
        ) {
            Text("Signaler un blocage")
        }
    }
}

/** Dialog with a required text field and a photo picker (gallery or camera). */
@Composable
private fun PhotoReportDialog(
    title: String,
    fieldLabel: String,
    photoRequired: Boolean,
    confirmLabel: String,
    confirmColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (text: String, photoUri: String?) -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri?.toString()
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val file = File(context.cacheDir, "task_photo_${System.currentTimeMillis()}.jpg")
            runCatching {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                }
                photoUri = Uri.fromFile(file).toString()
            }
        }
    }

    val canConfirm = text.isNotBlank() && (!photoRequired || photoUri != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(fieldLabel) },
                    minLines = 2
                )
                Text(
                    text = if (photoRequired) "Photo de preuve *" else "Photo (optionnelle)",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Galerie")
                    }
                    OutlinedButton(
                        onClick = { cameraLauncher.launch(null) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Filled.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Caméra")
                    }
                }
                if (photoUri != null) {
                    LocalImage(
                        uri = photoUri,
                        contentDescription = "Photo sélectionnée",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text.trim(), photoUri) },
                enabled = canConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
private fun CompletionCard(task: Task) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccentGreen.copy(alpha = 0.08f))
            .border(1.dp, AccentGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Validation",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AccentGreen
            )
        }
        task.completionComment?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
        task.completionPhotoUri?.let { uri ->
            Spacer(Modifier.height(12.dp))
            LocalImage(
                uri = uri,
                contentDescription = "Photo de preuve",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Composable
private fun BlockingCard(task: Task) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccentRed.copy(alpha = 0.08f))
            .border(1.dp, AccentRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Warning,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Blocage signalé",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AccentRed
            )
        }
        task.blockingReason?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = TextDark)
        }
        task.blockingPhotoUri?.let { uri ->
            Spacer(Modifier.height(12.dp))
            LocalImage(
                uri = uri,
                contentDescription = "Photo du blocage",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}
