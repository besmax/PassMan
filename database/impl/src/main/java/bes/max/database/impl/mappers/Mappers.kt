package bes.max.database.impl.mappers

import bes.max.database.api.model.SiteInfoModel
import bes.max.database.impl.entities.SiteInfoEntity

fun SiteInfoEntity.map(): SiteInfoModel = SiteInfoModel(
    id = id,
    name = name,
    url = url,
    iconUrl = iconUrl,
)


fun SiteInfoModel.map(): SiteInfoEntity = SiteInfoEntity(
    id = id,
    name = name,
    url = url,
    iconUrl = iconUrl,
)