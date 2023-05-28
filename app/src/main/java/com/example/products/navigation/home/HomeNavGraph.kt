package com.example.products.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.products.ui.screen.home.HomeScreen
import com.example.products.utils.account.logout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Граф навигаци для домашнего экрана
 */
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    context: Context,
) {
    navigation(
        route = AppNavigation.Home.route,
        startDestination = AppNavigation.Home.Catalog.route
    ) {
        composable(
            route = AppNavigation.Home.Catalog.route
        ) {
            HomeNavGraph(navController)
        }
    }
}

@Composable
fun HomeNavGraph(
    rootNavController: NavHostController,
) {
    HomeScreen(rootNavController = rootNavController)
}

