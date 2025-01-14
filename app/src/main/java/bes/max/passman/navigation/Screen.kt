package bes.max.passman.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Screen(
    val route: String,
    @StringRes val titleResId: Int? = null,
    @DrawableRes val iconResId: Int? = null
) {
    data object SitesScreen : Screen("sitesScreen")
    data object EditOrNewSiteScreen : Screen("editOrNewSiteScreen?{id}")
    data object CategoryScreen : Screen("categoryScreen")
    data object FileExplorerScreen : Screen("fileProviderScreen")
    data object SettingsScreen : Screen("settingsScreen")
}