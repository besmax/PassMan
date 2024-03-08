package bes.max.database.api.model

data class SiteInfoModel(
    val id: Int = 0,
    val name: String,
    val password: String,
    val url: String,
    val iconUrl: String,
)
