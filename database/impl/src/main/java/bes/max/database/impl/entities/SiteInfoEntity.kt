package bes.max.database.impl.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("site_info_table")
data class SiteInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo("password") val password: String,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("url") val url: String,
    @ColumnInfo("password_iv") val passwordIv: String,
)
