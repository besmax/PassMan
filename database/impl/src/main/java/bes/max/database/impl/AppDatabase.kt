package bes.max.database.impl

import androidx.room.Database
import androidx.room.RoomDatabase
import bes.max.database.impl.entities.SiteInfoEntity
import bes.max.database.impl.dao.SiteInfoDao

@Database(
    entities = [
        SiteInfoEntity::class,
    ],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteInfoDao(): SiteInfoDao
}