package bes.max.database.impl.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import bes.max.database.impl.entities.SiteInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteInfoDao {
    @Query("SELECT * FROM site_info_table")
    fun getAll(): Flow<List<SiteInfoEntity>>

    @Insert
    suspend fun insertAll(entities: List<SiteInfoEntity>)

    @Insert
    suspend fun insert(entity: SiteInfoEntity)

    @Upsert
    suspend fun update(entity: SiteInfoEntity)

    @Query("SELECT * FROM site_info_table WHERE name=:name")
    suspend fun getByName(name: String): SiteInfoEntity?

    @Query("SELECT * FROM site_info_table WHERE id=:id")
    suspend fun getById(id: Int): SiteInfoEntity?
}