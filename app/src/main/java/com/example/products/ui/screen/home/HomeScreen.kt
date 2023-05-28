package com.example.products.ui.screen.home

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.products.CartActivity
import com.example.products.navigation.AppNavigation
import com.example.products.navigation.BottomBar
import com.example.products.ui.screen.home.account.AccountScreen
import com.example.products.ui.screen.home.catalog.CatalogScreen
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    rootNavController: NavHostController,
    homeNavController: NavHostController = rememberNavController(),
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { BottomBar(homeNavController) },
        topBar = { TopBar() }
    ) { pv ->
        NavHost(
            navController = homeNavController,
            startDestination = AppNavigation.Home.Catalog.route,
            route = AppNavigation.Home.route
        ) {


            composable(route = AppNavigation.Home.Catalog.route) {
                EnterAnimation(
                    visible = homeNavController.currentBackStackEntry?.destination?.route
                        .toString() == AppNavigation.Home.Catalog.route
                ) {
                    CatalogScreen(navController = homeNavController, pv)
                }
            }
            composable(route = AppNavigation.Home.Account.route) {
                EnterAnimation(
                    visible = homeNavController.currentBackStackEntry?.destination?.route
                        .toString() == AppNavigation.Home.Account.route
                ) {
                    AccountScreen(
                        rootNavController,
                        homeNavController,
                        snackbarHostState,
                        paddingValues = pv
                    )
                }
            }
        }
    }
}

@Composable
fun EnterAnimation(
    visible: Boolean,
    content: @Composable () -> Unit,
) {

    val visibleState = remember {
        MutableTransitionState<Boolean>(
            initialState = !visible
        )
    }.apply {
        targetState = visible
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = slideInHorizontally(initialOffsetX = {
            (it * 1.75).roundToInt()
        }) +
                expandHorizontally(expandFrom = Alignment.End) +
                fadeIn(initialAlpha = 0.3f),
        exit = slideOutHorizontally() +
                shrinkHorizontally() +
                fadeOut(),
    ) {
        content.invoke()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val context = LocalContext.current
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        title = {
            Text(
                text = "Продукты",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        actions = {
            IconButton(onClick = {
                context.startActivity(
                    Intent(
                        context,
                        CartActivity::class.java
                    ).apply {
                    }
                )
            }) {
                Icon(
                    imageVector = Icons.TwoTone.ShoppingCart,
                    contentDescription = Icons.TwoTone.ShoppingCart.name,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        AppNavigation.Home.Catalog,
        AppNavigation.Home.Account
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarDestination = screens.any { it.route == currentDestination?.route }
    if (bottomBarDestination) {
        NavigationBar {
            screens.forEach { screen ->
                AddItem(
                    screen = screen,
                    route = screen,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBar,
    route: AppNavigation,
    currentDestination: NavDestination?,
    navController: NavHostController,
) {
    NavigationBarItem(
        label = {
            Text(text = screen.getTitle())
        },
        icon = {
            Icon(
                imageVector = screen.getIcon(),
                contentDescription = "Navigation Icon"
            )
        },
        selected = currentDestination?.hierarchy?.any {
            it.route == route.route
        } == true,
//        unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.disabled),
        onClick = {
            navController.navigate(route.route) {
                popUpTo(route.route)
//                launchSingleTop = true

            }
        }
    )
}


