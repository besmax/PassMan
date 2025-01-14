package bes.max.features.main.presentation.edit

import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.models.SiteInfoModelMain

sealed interface EditScreenState {
    data class Edit(val model: SiteInfoModelMain, val categories: List<CategoryModelMain > = emptyList()) :
        EditScreenState

    data object Error : EditScreenState

    data class New(val categories: List<CategoryModelMain > = emptyList()) : EditScreenState

    data object Loading : EditScreenState
}