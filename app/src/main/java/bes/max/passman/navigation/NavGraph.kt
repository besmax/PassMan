package bes.max.passman.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import bes.max.export.presentation.ExportEvent
import bes.max.export.presentation.ExportViewModel
import bes.max.export.ui.FileExplorerScreen
import bes.max.features.main.ui.CategoryScreen
import bes.max.features.main.ui.EditOrNewSiteScreen
import bes.max.features.main.ui.SettingsScreen
import bes.max.features.main.ui.SitesScreen

@Composable
fun NavigationGraph(
    navHostController: NavHostController,
    launchAuth: (() -> Unit, () -> Unit) -> Unit,
) {
    NavHost(navController = navHostController, startDestination = Screen.SitesScreen.route) {
        composable(route = Screen.SitesScreen.route) {
            SitesScreen(
                navigateToEdit = { id ->
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
                navigateToSettings = {
                    navHostController.navigate(
                        Screen.SettingsScreen.route
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
            EditOrNewSiteScreen(
                navigateBack = { navHostController.popBackStack() },
                launchAuth = launchAuth
            )
        }

        composable(
            route = Screen.CategoryScreen.route
        ) {
            CategoryScreen(navigateBack = { navHostController.popBackStack() })
        }

        composable(
            route = Screen.FileExplorerScreen.route
        ) {
            FileExplorerScreen(navigateBack = { navHostController.popBackStack() })
        }

        composable(
            route = Screen.SettingsScreen.route
        ) {
            val exportViewModel: ExportViewModel = hiltViewModel()
            val code by exportViewModel.code.observeAsState()
            val event by exportViewModel.event.observeAsState()

            SettingsScreen(
                navigateBack = { navHostController.popBackStack() },
                export = exportViewModel::export,
                import = exportViewModel::import,
                importCode = code,
                resetImportCode = exportViewModel::resetCode,
                eventMessage = if (event is ExportEvent.WrongImportCodeEvent) {
                    stringResource((event as ExportEvent.WrongImportCodeEvent).messageResId)
                } else {
                    null
                },
                resetEvent = exportViewModel::resetEvent,
                launchBiometric = launchAuth
            )
        }
    }
}