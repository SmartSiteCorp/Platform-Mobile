package com.smartsite.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun DashboardScreen(
    onTaskClick: (String) -> Unit,
    onSiteClick: (String) -> Unit,
    onSeeAllTasks: () -> Unit,
    onSeeAllSites: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Tableau de bord", style = MaterialTheme.typography.titleLarge)
    }
}
