package bes.max.features.main.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import bes.max.database.api.model.CategoryModel
import bes.max.features.main.domain.models.CategoryModelMain

fun CategoryModelMain.map(): CategoryModel = CategoryModel(
    id = id,
    name = name,
    color = color.toArgb(),
)

fun CategoryModel.map(): CategoryModelMain = CategoryModelMain(
    id = id,
    name = name,
    color = Color(color),
)