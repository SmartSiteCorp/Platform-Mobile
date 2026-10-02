package com.smartsite.app.data.mock

import com.smartsite.app.data.model.Annotation
import com.smartsite.app.data.model.AnnotationSupport
import com.smartsite.app.data.model.AnnotationType
import com.smartsite.app.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.util.UUID

/**
 * In-memory replacement for the Base44 SDK `Annotation` entity.
 */
object AnnotationRepository {

    private val _annotations = MutableStateFlow(
        listOf(
            Annotation(
                id = "anno-poutre",
                type = AnnotationType.CIRCLE,
                content = "Vérifier l'alignement de la poutre P12 avec le plan de coffrage.",
                support = AnnotationSupport.PLAN,
                zone = "Bâtiment A",
                color = "#EF4444",
                siteId = "site-jardins",
                authorRole = UserRole.ARCHITECT,
                createdDate = LocalDate.of(2026, 9, 26)
            ),
            Annotation(
                id = "anno-acces",
                type = AnnotationType.ARROW,
                content = "Accès engins à déplacer côté sud pendant la phase terrassement.",
                support = AnnotationSupport.PLAN,
                zone = "Parcelle B",
                color = "#D4A03A",
                siteId = "site-olympe",
                authorRole = UserRole.PROJECT_MANAGER,
                createdDate = LocalDate.of(2026, 9, 19)
            ),
            Annotation(
                id = "anno-fissure",
                type = AnnotationType.FREEHAND,
                content = "Fissure superficielle constatée sur le voile — à surveiller au prochain contrôle.",
                support = AnnotationSupport.IMAGE,
                zone = "Bâtiment C",
                color = "#5B89C8",
                siteId = "site-ecole",
                authorRole = UserRole.WORKER,
                createdDate = LocalDate.of(2026, 10, 2)
            ),
            Annotation(
                id = "anno-epdm",
                type = AnnotationType.TEXT,
                content = "Zone EPDM à reprendre après la période de grand vent.",
                support = AnnotationSupport.DRONE_CAPTURE,
                zone = "Toiture",
                color = "#EF4444",
                siteId = "site-horizon",
                authorRole = UserRole.DRONE_OPERATOR,
                createdDate = LocalDate.of(2026, 10, 1)
            ),
            Annotation(
                id = "anno-reseaux",
                type = AnnotationType.ARROW,
                content = "Passage des réseaux enterrés confirmé sous le radier nord.",
                support = AnnotationSupport.PLAN,
                zone = "Niveau -1",
                color = "#56A87A",
                siteId = "site-jardins",
                authorRole = UserRole.ARCHITECT,
                createdDate = LocalDate.of(2026, 9, 12)
            ),
            Annotation(
                id = "anno-halle",
                type = AnnotationType.TEXT,
                content = "Réserve levée sur l'alignement des pannes métalliques.",
                support = AnnotationSupport.IMAGE,
                zone = "Salle principale",
                color = "#7C6DB5",
                siteId = "site-verdun",
                authorRole = UserRole.ARCHITECT,
                createdDate = LocalDate.of(2026, 4, 15)
            )
        )
    )
    val annotations: StateFlow<List<Annotation>> = _annotations.asStateFlow()

    fun create(
        type: AnnotationType,
        content: String,
        support: AnnotationSupport,
        zone: String,
        color: String,
        siteId: String,
        authorRole: UserRole
    ): Annotation {
        val annotation = Annotation(
            id = "anno-${UUID.randomUUID()}",
            type = type,
            content = content,
            support = support,
            zone = zone,
            color = color,
            siteId = siteId,
            authorRole = authorRole,
            createdDate = LocalDate.now()
        )
        _annotations.value = _annotations.value + annotation
        return annotation
    }
}
