package bes.max.passman.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Screen(
    val route: String,
    @StringRes val titleResId: Int? = null,
    @DrawableRes val iconResId: Int? = null
) {
    object SitesScreen : Screen("sitesScreen")
    object EditSiteScreen : Screen("editSiteScreen/{id}")
}