package bes.max.features.main.presentation.sites

import bes.max.features.main.domain.models.FilterModel
import bes.max.features.main.domain.models.SiteInfoModelMain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SitesScreenState {

    data class Content(
        val sites: ImmutableList<SiteInfoModelMain>,
        val filteredSites: ImmutableList<SiteInfoModelMain>,
        val filters: ImmutableList<FilterModel> = persistentListOf(),
        val selectedCategory: Int = -1
    ) : SitesScreenState

    data object Empty : SitesScreenState

    data object Loading : SitesScreenState
}

data class SelectedState(
    val selectedIds: Set<Int> = emptySet(),
    val selecting: Boolean = false,
)
