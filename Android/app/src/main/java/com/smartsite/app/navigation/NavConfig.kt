package com.smartsite.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.ui.graphics.vector.ImageVector
import com.smartsite.app.data.model.UserRole

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val dashboard = NavItem(Routes.DASHBOARD, "Tableau de bord", Icons.Filled.Home)
private val sites = NavItem(Routes.SITES, "Chantiers", Icons.Filled.Map)
private val tasks = NavItem(Routes.TASKS, "Tâches", Icons.AutoMirrored.Filled.Assignment)
private val myTasks = NavItem(Routes.TASKS, "Mes tâches", Icons.AutoMirrored.Filled.Assignment)
private val media = NavItem(Routes.MEDIA, "Médias", Icons.Filled.Image)
private val drone = NavItem(Routes.DRONE, "Drone", Icons.Filled.Sensors)
private val annotations = NavItem(Routes.ANNOTATIONS, "Annotations", Icons.Filled.Draw)
private val profile = NavItem(Routes.PROFILE, "Profil", Icons.Filled.Person)

/**
 * Navigation surfaces per role (mirrors `src/Layout.jsx` in the mockup).
 * The alpha runs with the fixed admin user, but the structure is role-aware.
 */
object NavConfig {

    /** Items in the modal drawer — everything the role can reach. */
    fun drawerItems(role: UserRole): List<NavItem> = when (role) {
        UserRole.ADMIN -> listOf(dashboard, sites, tasks, media, annotations, drone, profile)
        UserRole.ARCHITECT -> listOf(dashboard, sites, tasks, media, annotations, drone, profile)
        UserRole.PROJECT_MANAGER -> listOf(dashboard, sites, tasks, media, drone, profile)
        UserRole.WORKER -> listOf(dashboard, myTasks, sites, drone, profile)
        UserRole.DRONE_OPERATOR -> listOf(drone, dashboard, sites, media, profile)
    }

    /** Fixed bottom bar — the 5 main destinations of the role. */
    fun bottomNavItems(role: UserRole): List<NavItem> = when (role) {
        UserRole.WORKER -> listOf(dashboard, myTasks, sites, drone, profile)
        UserRole.DRONE_OPERATOR -> listOf(drone, dashboard, sites, media, profile)
        else -> listOf(dashboard, sites, tasks, media, drone)
    }
}
