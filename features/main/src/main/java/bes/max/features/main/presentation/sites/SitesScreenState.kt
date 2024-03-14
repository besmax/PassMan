package bes.max.features.main.presentation.sites

import bes.max.features.main.domain.models.SiteInfoModelMain

sealed interface SitesScreenState {
    data class Content(val sites: List<SiteInfoModelMain>) : SitesScreenState
    data object Empty : SitesScreenState
    data object Loading : SitesScreenState
}