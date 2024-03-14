package bes.max.features.main.presentation.edit

import bes.max.features.main.domain.models.SiteInfoModelMain

sealed interface EditScreenState {
    data class Edit(val model: SiteInfoModelMain) : EditScreenState

    data object Error : EditScreenState

    data object New : EditScreenState

    data object Loading : EditScreenState
}