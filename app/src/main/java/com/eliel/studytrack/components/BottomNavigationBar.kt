package com.eliel.studytrack.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eliel.studytrack.R
import com.eliel.studytrack.Screen

data class BottomNavItem(
    val route: String,
    val iconVector: ImageVector? = null,
    val iconRes: Int? = null
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(Screen.Home.route, iconVector = Icons.Filled.Home),
        BottomNavItem(Screen.Schedule.route, iconRes = R.drawable.ic_schedule),
        BottomNavItem(Screen.Pomodoro.route, iconRes = R.drawable.ic_pomodoro),
        BottomNavItem(Screen.Reports.route, iconRes = R.drawable.ic_reports),
        BottomNavItem(Screen.Settings.route, iconVector = Icons.Filled.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    if (item.iconVector != null) {
                        Icon(
                            item.iconVector,
                            contentDescription = item.route,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (item.iconRes != null) {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.route,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ),
                alwaysShowLabel = false
            )
        }
    }
}
