package com.smartsite.app.data.mock

import com.smartsite.app.data.model.Task
import com.smartsite.app.data.model.TaskPriority
import com.smartsite.app.data.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

/**
 * In-memory replacement for the Base44 SDK `Task` entity.
 */
object TaskRepository {

    private val _tasks = MutableStateFlow(
        listOf(
            Task(
                id = "task-dalle",
                title = "Coulage dalle RDC",
                description = "Coulage de la dalle béton du rez-de-chaussée, bâtiment A. Prévoir 42 m³ de béton C25/30.",
                status = TaskStatus.IN_PROGRESS,
                priority = TaskPriority.HIGH,
                siteId = "site-jardins",
                zone = "Bâtiment A",
                assignedTo = "pierre.martin@smartsite.fr",
                dueDate = LocalDate.of(2026, 10, 9)
            ),
            Task(
                id = "task-menuiseries",
                title = "Pose menuiseries extérieures",
                description = "Pose des menuiseries aluminium du bâtiment B (34 fenêtres, 6 baies vitrées).",
                status = TaskStatus.PENDING,
                priority = TaskPriority.MEDIUM,
                siteId = "site-jardins",
                zone = "Bâtiment B",
                assignedTo = "marie.dupont@smartsite.fr",
                dueDate = LocalDate.of(2026, 10, 14)
            ),
            Task(
                id = "task-vmc",
                title = "Vérification réseau VMC",
                description = "Contrôle d'étanchéité du réseau VMC double flux au niveau R+3 avant fermeture des plafonds.",
                status = TaskStatus.PENDING,
                priority = TaskPriority.URGENT,
                siteId = "site-horizon",
                zone = "Niveau R+3",
                assignedTo = "marie.dupont@smartsite.fr",
                dueDate = LocalDate.of(2026, 10, 6)
            ),
            Task(
                id = "task-etancheite",
                title = "Étanchéité toiture",
                description = "Pose de la membrane EPDM sur la toiture terrasse, zone sud.",
                status = TaskStatus.BLOCKED,
                priority = TaskPriority.HIGH,
                siteId = "site-horizon",
                zone = "Toiture",
                assignedTo = "lucas.bernard@smartsite.fr",
                dueDate = LocalDate.of(2026, 10, 8),
                blockingReason = "Intempéries : vents violents annoncés toute la semaine, reprise impossible en sécurité."
            ),
            Task(
                id = "task-peinture",
                title = "Finitions peinture",
                description = "Deux couches de peinture acrylique dans la salle principale et le vestiaire.",
                status = TaskStatus.COMPLETED,
                priority = TaskPriority.LOW,
                siteId = "site-verdun",
                zone = "Salle principale",
                assignedTo = "sophie.leroy@smartsite.fr",
                dueDate = LocalDate.of(2026, 4, 18),
                completionComment = "Travaux réceptionnés avec le conducteur d'opération, aucune réserve."
            ),
            Task(
                id = "task-terrassement",
                title = "Terrassement parcelle B",
                description = "Décapage et mise à niveau de la parcelle B pour les cellules 5 à 8.",
                status = TaskStatus.PENDING,
                priority = TaskPriority.MEDIUM,
                siteId = "site-olympe",
                zone = "Parcelle B",
                assignedTo = "pierre.martin@smartsite.fr",
                dueDate = LocalDate.of(2026, 11, 20)
            ),
            Task(
                id = "task-beton",
                title = "Contrôle béton armé",
                description = "Contrôle visuel et sondes du voile béton armé du bâtiment C après décoffrage.",
                status = TaskStatus.IN_PROGRESS,
                priority = TaskPriority.HIGH,
                siteId = "site-ecole",
                zone = "Bâtiment C",
                assignedTo = "marie.dupont@smartsite.fr",
                dueDate = LocalDate.of(2026, 10, 10)
            ),
            Task(
                id = "task-plomberie",
                title = "Réception lot plomberie",
                description = "Réception du lot plomberie-sanitaire de la résidence, épreuves d'eau incluses.",
                status = TaskStatus.COMPLETED,
                priority = TaskPriority.MEDIUM,
                siteId = "site-jardins",
                zone = "Bâtiment A",
                assignedTo = "sophie.leroy@smartsite.fr",
                dueDate = LocalDate.of(2026, 9, 28),
                completionComment = "Épreuves d'eau conformes, PV signé."
            )
        )
    )
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    fun getById(id: String): Task? = _tasks.value.firstOrNull { it.id == id }

    fun create(
        title: String,
        siteId: String,
        description: String,
        priority: TaskPriority,
        zone: String,
        assignedTo: String,
        dueDate: LocalDate
    ): Task {
        val task = Task(
            id = "task-${UUID.randomUUID()}",
            title = title,
            description = description,
            status = TaskStatus.PENDING,
            priority = priority,
            siteId = siteId,
            zone = zone,
            assignedTo = assignedTo,
            dueDate = dueDate
        )
        _tasks.value = _tasks.value + task
        return task
    }

    /** Replaces the task with the same id. */
    fun update(task: Task) {
        _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
    }
}
