package bes.max.database.impl.mappers

import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.database.impl.entities.CategoryEntity
import bes.max.database.impl.entities.SiteInfoEntity

fun SiteInfoEntity.map(): SiteInfoModel = SiteInfoModel(
    id = id,
    name = name,
    url = url,
    password = password,
    passwordIv = passwordIv,
    description = description,
)


fun SiteInfoModel.map(): SiteInfoEntity = SiteInfoEntity(
    id = id,
    name = name,
    url = url,
    password = password,
    passwordIv = passwordIv,
    description = description,
)

fun CategoryEntity.map(): CategoryModel = CategoryModel(
    id = id,
    name = name,
    color = color,
)

fun CategoryModel.map(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    color = color,
)