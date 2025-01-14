package bes.max.features.main.domain.models

data class SiteInfoModelMain(
    val id: Int = -1,
    val name: String,
    val password: String,
    val url: String,
    val passwordIv: String,
    val description: String?,
    val categoryColor: Int? = null,
) {
    val iconUrl: String get() = "$url/favicon.ico"
}
