package com.smartsite.app.data.model

import java.time.LocalDate

enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED, BLOCKED }

enum class TaskPriority { LOW, MEDIUM, HIGH, URGENT }

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val priority: TaskPriority,
    val siteId: String,
    val zone: String,
    val assignedTo: String, // assignee email
    val dueDate: LocalDate,
    val completionComment: String? = null,
    val completionPhotoUri: String? = null,
    val blockingReason: String? = null,
    val blockingPhotoUri: String? = null
)
