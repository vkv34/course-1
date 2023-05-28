package com.example.products.navigation

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.products.ui.screen.auth.LoginScreen
import com.example.products.ui.screen.auth.SignInScreen

/**
 * Граф навигации экрана авторизации
 */
fun NavGraphBuilder.accountNavGraph(
    navController: NavHostController,
    context: Context
) {
    navigation(
        startDestination = AppNavigation.Auth.Login.route,
        route = AppNavigation.Auth.route
    ) {
        composable(
            route = AppNavigation.Auth.Login.route
        ) {
            LoginScreen(navController)
        }
        composable(
            route = AppNavigation.Auth.SignIn.route
        ) {
            SignInScreen(navController)
        }
    }
}