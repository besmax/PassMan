package bes.max.features.main.presentation.sites

import bes.max.features.main.domain.models.FilterModel
import bes.max.features.main.domain.models.SiteInfoModelMain

sealed interface SitesScreenState {

    data class Content(
        val sites: List<SiteInfoModelMain>,
        val filteredSites: List<SiteInfoModelMain>,
        val filters: List<FilterModel> = emptyList(),
    ) : SitesScreenState

    data object Empty : SitesScreenState

    data object Loading : SitesScreenState
}
