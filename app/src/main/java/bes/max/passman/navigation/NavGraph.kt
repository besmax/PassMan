package bes.max.passman.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bes.max.features.main.ui.CategoryScreen
import bes.max.features.main.ui.EditOrNewSiteScreen
import bes.max.features.main.ui.SitesScreen

@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
) {
    NavHost(navController = navHostController, startDestination = Screen.SitesScreen.route) {
        composable(route = Screen.SitesScreen.route) {
            SitesScreen(navigateToEdit = { id ->
                navHostController.navigate(
                    Screen.EditOrNewSiteScreen.route.replace(
                        "{id}",
                        id.toString()
                    )
                )
            },
                navigateToNew = {
                    navHostController.navigate(
                        Screen.EditOrNewSiteScreen.route
                    )
                },
                navigateToCategory = {
                    navHostController.navigate(
                        Screen.CategoryScreen.route
                    )
                },
                launchAuth = launchAuth,
            )
        }

        composable(
            route = Screen.EditOrNewSiteScreen.route,
            arguments = listOf(
                navArgument(name = "id") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = -1
                }
            )
        ) {
            EditOrNewSiteScreen(navigateBack = { navHostController.popBackStack() }, launchAuth = launchAuth)
        }

        composable(
            route = Screen.CategoryScreen.route
        ) {
            CategoryScreen(navigateBack = { navHostController.popBackStack() })
        }
    }
}