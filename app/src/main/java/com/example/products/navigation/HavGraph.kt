package com.example.products.navigation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.products.utils.account.isLogin

/**
 * Установка основного графа навгации
 */
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    context: Context = LocalContext.current.applicationContext,
) {
    NavHost(
        navController = navController,
        startDestination =
        if (isLogin()) {
            Log.d("isLogin", isLogin().toString())
            AppNavigation.Home.route
        } else AppNavigation.Auth.route,
        route = ROOT_ROUTE
    ) {
        accountNavGraph(navController, context)
        homeNavGraph(navController, context)
    }

}



