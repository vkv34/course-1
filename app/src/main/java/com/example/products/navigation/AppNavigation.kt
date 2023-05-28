package com.example.products.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

const val ROOT_ROUTE = "ROOT"

/**
 * Инерфейс для нижнего меню
 */
interface BottomBar {

    fun getTitle(): String

    fun getIcon(): ImageVector
}

/**
 * Класс навигации по приложению
 *
 * @param route путь навигации
 */
sealed class AppNavigation(val route: String) {
    object Home : AppNavigation(Home::class.simpleName.toString()) {

        object Catalog : AppNavigation(Catalog::class.simpleName.toString()), BottomBar {

            override fun getTitle(): String = "Каталог"

            override fun getIcon(): ImageVector = Icons.Default.Info

            object Main: AppNavigation(Main::class.simpleName.toString())

        }

        object Account : AppNavigation(Account::class.simpleName.toString()), BottomBar {

            override fun getTitle(): String = "Аккаунт"

            override fun getIcon(): ImageVector = Icons.Default.AccountBox
        }
    }


    object Auth : AppNavigation(Auth::class.simpleName.toString()) {
        object SignIn : AppNavigation(SignIn::class.simpleName.toString())
        object Login : AppNavigation(Login::class.simpleName.toString())
    }

}
