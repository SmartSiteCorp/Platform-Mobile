package com.smartsite.app.data.repository

import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskPriority
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface TaskRepository {
    val tasks: StateFlow<List<Task>>
    fun getById(id: String): Task?
    fun create(
        title: String,
        siteId: String,
        description: String,
        priority: TaskPriority,
        zone: String,
        assignedTo: String,
        dueDate: LocalDate
    ): Task
    fun update(task: Task)
}
