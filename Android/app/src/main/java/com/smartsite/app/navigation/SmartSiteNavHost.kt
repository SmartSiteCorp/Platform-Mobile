package com.smartsite.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartsite.app.ui.screens.annotations.AnnotationsScreen
import com.smartsite.app.ui.screens.dashboard.DashboardScreen
import com.smartsite.app.ui.screens.drone.DroneViewScreen
import com.smartsite.app.ui.screens.media.MediaGalleryScreen
import com.smartsite.app.ui.screens.profile.ProfileScreen
import com.smartsite.app.ui.screens.sitedetail.SiteDetailScreen
import com.smartsite.app.ui.screens.sites.SitesScreen
import com.smartsite.app.ui.screens.taskdetail.TaskDetailScreen
import com.smartsite.app.ui.screens.tasks.TasksScreen

@Composable
fun SmartSiteNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
        modifier = modifier
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onTaskClick = { navController.navigate(Routes.taskDetail(it)) },
                onSiteClick = { navController.navigate(Routes.siteDetail(it)) },
                onSeeAllTasks = { navController.navigate(Routes.TASKS) },
                onSeeAllSites = { navController.navigate(Routes.SITES) }
            )
        }
        composable(Routes.SITES) {
            SitesScreen(onSiteClick = { navController.navigate(Routes.siteDetail(it)) })
        }
        composable(
            Routes.SITE_DETAIL,
            arguments = listOf(navArgument("siteId") { type = NavType.StringType })
        ) { entry ->
            SiteDetailScreen(
                siteId = entry.arguments?.getString("siteId").orEmpty(),
                onBack = { navController.popBackStack() },
                onTaskClick = { navController.navigate(Routes.taskDetail(it)) }
            )
        }
        composable(Routes.TASKS) {
            TasksScreen(onTaskClick = { navController.navigate(Routes.taskDetail(it)) })
        }
        composable(
            Routes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            TaskDetailScreen(
                taskId = entry.arguments?.getString("taskId").orEmpty(),
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.DRONE) { DroneViewScreen() }
        composable(Routes.MEDIA) { MediaGalleryScreen() }
        composable(Routes.ANNOTATIONS) { AnnotationsScreen() }
        composable(Routes.PROFILE) { ProfileScreen() }
    }
}
