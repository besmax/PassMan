package bes.max.database.api.model

data class SiteInfoModel(
    val id: Int = 0,
    val name: String,
    val password: String,
    val url: String,
    val passwordIv: String,
    val description: String?,
    val categoryColor: Int? = null
) {
    val iconUrl: String get() = "$url/favicon.ico"
}
