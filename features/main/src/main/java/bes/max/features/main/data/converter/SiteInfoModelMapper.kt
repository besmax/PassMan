package bes.max.features.main.data.converter

import bes.max.database.api.model.SiteInfoModel
import bes.max.features.main.domain.models.SiteInfoModelMain

fun SiteInfoModelMain.map(): SiteInfoModel = SiteInfoModel(
    id = id,
    name = name,
    password = password,
    url = url,
    passwordIv = passwordIv,
    description = description,
    categoryColor = categoryColor,
    login = login,
)

fun SiteInfoModel.map(): SiteInfoModelMain = SiteInfoModelMain(
    id = id,
    name = name,
    password = password,
    url = url,
    passwordIv = passwordIv,
    description = description,
    categoryColor = categoryColor,
    login = login,
)