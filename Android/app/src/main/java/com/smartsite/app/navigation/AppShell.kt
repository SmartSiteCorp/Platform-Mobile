package com.smartsite.app.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartsite.app.data.repository.Repositories
import com.smartsite.app.ui.components.BrandLogo
import com.smartsite.app.ui.theme.AccentRed
import com.smartsite.app.ui.theme.NavInactive
import com.smartsite.app.ui.theme.Orange
import com.smartsite.app.ui.theme.Sage
import com.smartsite.app.ui.theme.SageDark
import com.smartsite.app.ui.theme.TextDark
import com.smartsite.app.ui.theme.TextMuted
import com.smartsite.app.util.frenchLabel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartSiteApp() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val user by Repositories.auth.currentUser.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = SageDark) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 24.dp)) {
                    BrandLogo()
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Navigation",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                }
                NavConfig.drawerItems(user.role).forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label, color = Color.White) },
                        icon = { Icon(item.icon, contentDescription = null, tint = Color.White) },
                        selected = currentRoute == item.route,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navigate(item.route)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Orange,
                            unselectedContainerColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Sage,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    title = {
                        BrandLogo(
                            tileSize = 30.dp,
                            modifier = Modifier.clickable { navigate(Routes.DASHBOARD) }
                        )
                    },
                    actions = {
                        var menuOpen by remember { mutableStateOf(false) }
                        Box {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SageDark)
                                    .clickable { menuOpen = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.fullName.first().uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                                Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                    Text(user.fullName, fontWeight = FontWeight.Bold, color = TextDark)
                                    Text(user.role.frenchLabel(), style = MaterialTheme.typography.bodySmall, color = TextMuted)
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Profil") },
                                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                                    onClick = {
                                        menuOpen = false
                                        navController.navigate(Routes.PROFILE)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Déconnexion", color = AccentRed) },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AccentRed) },
                                    onClick = { menuOpen = false /* stub in the alpha */ }
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    NavConfig.bottomNavItems(user.role).forEach { item ->
                        val selected = currentRoute == item.route ||
                            (item.route == Routes.SITES && currentRoute?.startsWith("site_detail") == true) ||
                            (item.route == Routes.TASKS && currentRoute?.startsWith("task_detail") == true)
                        NavigationBarItem(
                            selected = selected,
                            onClick = { navigate(item.route) },
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Orange,
                                selectedTextColor = Orange,
                                unselectedIconColor = NavInactive,
                                unselectedTextColor = NavInactive,
                                indicatorColor = Orange.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(Modifier.fillMaxSize().padding(innerPadding)) {
                SmartSiteNavHost(navController)
            }
        }
    }
}
