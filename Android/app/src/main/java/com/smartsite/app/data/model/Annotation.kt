package com.smartsite.app.data.model

import java.time.LocalDate

enum class AnnotationType { TEXT, ARROW, CIRCLE, FREEHAND }

enum class AnnotationSupport { PLAN, IMAGE, DRONE_CAPTURE }

data class Annotation(
    val id: String,
    val type: AnnotationType,
    val content: String,
    val support: AnnotationSupport,
    val zone: String,
    val color: String, // hex, e.g. "#EF4444"
    val siteId: String,
    val authorRole: UserRole,
    val createdDate: LocalDate
)
