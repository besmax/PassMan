package bes.max.features.main.domain.models

import androidx.compose.ui.graphics.Color

data class FilterModel(
    val name: String? = null,
    val color: Color,
    val filterAction: () -> Unit,
)