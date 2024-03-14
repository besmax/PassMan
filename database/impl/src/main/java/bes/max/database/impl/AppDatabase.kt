package bes.max.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.entities.SiteInfoEntity

@Database(
    entities = [
        SiteInfoEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteInfoDao(): SiteInfoDao
}