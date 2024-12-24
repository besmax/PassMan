package bes.max.database.impl.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("category_table")
data class CategoryEntity(
    @ColumnInfo("name") val name: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("color") val color: Int,
)
