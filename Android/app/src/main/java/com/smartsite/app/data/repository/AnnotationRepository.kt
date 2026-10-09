package com.smartsite.app.data.repository

import com.smartsite.app.data.model.Annotation
import com.smartsite.app.data.model.AnnotationSupport
import com.smartsite.app.data.model.AnnotationType
import com.smartsite.app.data.model.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AnnotationRepository {
    val annotations: StateFlow<List<Annotation>>
    fun create(
        type: AnnotationType,
        content: String,
        support: AnnotationSupport,
        zone: String,
        color: String,
        siteId: String,
        authorRole: UserRole
    ): Annotation
}
