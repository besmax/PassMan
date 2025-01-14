package bes.max.features.main.presentation.category

import androidx.compose.ui.graphics.Color
import bes.max.features.main.domain.models.CategoryModelMain

interface CategoryScreenState {
    data class Content(
        val categories: List<CategoryModelMain>,
        val colors: List<Color>,
    ) : CategoryScreenState

    data object Loading : CategoryScreenState
}