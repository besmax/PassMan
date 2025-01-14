package bes.max.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import bes.max.database.impl.dao.CategoryDao
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.entities.CategoryEntity
import bes.max.database.impl.entities.SiteInfoEntity

@Database(
    entities = [
        SiteInfoEntity::class,
        CategoryEntity::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteInfoDao(): SiteInfoDao
    abstract fun categoryDao(): CategoryDao
}