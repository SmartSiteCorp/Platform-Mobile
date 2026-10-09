package com.smartsite.app.data.repository

import com.smartsite.app.data.mock.MockAnnotationRepository
import com.smartsite.app.data.mock.MockAuthRepository
import com.smartsite.app.data.mock.MockDroneRepository
import com.smartsite.app.data.mock.MockMediaRepository
import com.smartsite.app.data.mock.MockSiteRepository
import com.smartsite.app.data.mock.MockTaskRepository

/**
 * Service locator: the single entry point screens use to reach the data layer.
 * Swap these bindings for remote implementations when integrating the API.
 */
object Repositories {
    val sites: SiteRepository = MockSiteRepository
    val tasks: TaskRepository = MockTaskRepository
    val media: MediaRepository = MockMediaRepository
    val annotations: AnnotationRepository = MockAnnotationRepository
    val drone: DroneRepository = MockDroneRepository
    val auth: AuthRepository = MockAuthRepository
}
