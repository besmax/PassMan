package bes.max.database.api.model

import kotlinx.serialization.Serializable

@Serializable
data class SiteInfoModel(
    val id: Int = -1,
    val name: String,
    val password: String,
    val url: String,
    val passwordIv: String,
    val description: String?,
    val login: String?,
    val categoryColor: Int? = null,
) {
    val iconUrl: String get() = "$url/favicon.ico"
}
