package bes.max.database.impl.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import bes.max.database.impl.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM category_table")
    fun getAll(): Flow<List<CategoryEntity>>

    @Insert
    suspend fun insertAll(entities: List<CategoryEntity>)

    @Insert
    suspend fun insert(entity: CategoryEntity)

    @Upsert
    suspend fun update(entity: CategoryEntity)

    @Query("SELECT * FROM category_table WHERE name=:name")
    suspend fun getByName(name: String): CategoryEntity?

    @Query("SELECT * FROM category_table WHERE color=:id")
    suspend fun getById(id: Int): CategoryEntity?

    @Delete
    suspend fun delete(entity: CategoryEntity)
}