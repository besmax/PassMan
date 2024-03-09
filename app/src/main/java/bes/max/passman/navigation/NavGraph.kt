package bes.max.passman.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bes.max.main.ui.EditSiteScreen
import bes.max.main.ui.SitesScreen

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = Screen.SitesScreen.route) {
        composable(route = Screen.SitesScreen.route) {
            SitesScreen()
        }

        composable(
            route = Screen.EditSiteScreen.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    nullable = false
                }
            )) {

            EditSiteScreen()
        }
    }
}