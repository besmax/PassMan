package bes.max.features.main.presentation.sites

import bes.max.database.api.model.SiteInfoModel

sealed interface SitesScreenState {
    data class Content(val sites: List<SiteInfoModel>) : SitesScreenState
    data object Empty : SitesScreenState
    data object Loading : SitesScreenState
}