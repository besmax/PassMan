package bes.max.database.api.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryModel(
    val name: String,
    val color: Int,
)
