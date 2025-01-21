package bes.max.features.main.data.converter

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import bes.max.database.api.model.CategoryModel
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.models.FilterModel

fun CategoryModelMain.map(): CategoryModel = CategoryModel(
    name = name,
    color = color.toArgb(),
)

fun CategoryModel.map(): CategoryModelMain = CategoryModelMain(
    name = name,
    color = Color(color),
)

fun CategoryModelMain.toFilter(filterAction: (Int) -> Unit): FilterModel = FilterModel(
    name = name,
    color = color,
    filterAction = { color -> filterAction(color) },
)