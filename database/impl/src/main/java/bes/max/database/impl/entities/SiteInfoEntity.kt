package bes.max.database.impl.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("site_info_table")
data class SiteInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @ColumnInfo("password") val password: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("url") val url: String,
    @ColumnInfo("password_iv") val passwordIv: String,
    @ColumnInfo("description") val description: String?,
    @ColumnInfo("category") val category: Int?,
    @ColumnInfo("login") val login: String? = null,
)
