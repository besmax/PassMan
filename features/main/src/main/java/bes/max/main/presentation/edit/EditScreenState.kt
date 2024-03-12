package bes.max.main.presentation.edit

import bes.max.database.api.model.SiteInfoModel

sealed interface EditScreenState {
    data class Edit(val model: SiteInfoModel) : EditScreenState

    data object Error : EditScreenState

    data object New : EditScreenState

    data object Loading : EditScreenState
}