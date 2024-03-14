package bes.max.features.main.domain.models

data class SiteInfoModelMain(
    val id: Int = 0,
    val name: String,
    val password: String,
    val url: String,
    val passwordIv: String
) {
    val iconUrl: String get() = "$url/favicon.ico"
}
