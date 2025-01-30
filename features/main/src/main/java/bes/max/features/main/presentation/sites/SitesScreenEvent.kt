package bes.max.features.main.presentation.sites

import bes.max.passman.features.main.R

sealed interface SitesScreenEvent {
    data object Default: SitesScreenEvent
    data class WrongUrl(val messageResId: Int = R.string.failed_open_url): SitesScreenEvent
}