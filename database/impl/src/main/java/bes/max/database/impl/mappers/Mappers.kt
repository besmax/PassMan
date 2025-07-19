package bes.max.database.impl.mappers

import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.database.impl.entities.CategoryEntity
import bes.max.database.impl.entities.SiteInfoEntity

fun SiteInfoEntity.map(): SiteInfoModel = SiteInfoModel(
    id = id ?: -1,
    name = name,
    url = url,
    password = password,
    passwordIv = passwordIv,
    description = description,
    categoryColor = category,
    login = login,
)

fun SiteInfoModel.map(): SiteInfoEntity = SiteInfoEntity(
    id = if (id < 0) null else id,
    name = name,
    url = url,
    password = password,
    passwordIv = passwordIv,
    description = description,
    category = categoryColor,
    login = login,
)

fun CategoryEntity.map(): CategoryModel = CategoryModel(
    name = name,
    color = color,
)

fun CategoryModel.map(): CategoryEntity = CategoryEntity(
    name = name,
    color = color,
)
