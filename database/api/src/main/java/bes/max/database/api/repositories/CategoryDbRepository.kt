package bes.max.database.api.repositories

import bes.max.database.api.model.CategoryModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface CategoryDbRepository {
    fun getAll(dispatcher: CoroutineDispatcher): Flow<List<CategoryModel>>

    suspend fun getByName(name: String, dispatcher: CoroutineDispatcher): CategoryModel?

    suspend fun getById(id: Int, dispatcher: CoroutineDispatcher): CategoryModel?

    suspend fun insertAll(models: List<CategoryModel>, dispatcher: CoroutineDispatcher)

    suspend fun update(model: CategoryModel, dispatcher: CoroutineDispatcher)

    suspend fun insert(model: CategoryModel, dispatcher: CoroutineDispatcher)

    suspend fun delete(model: CategoryModel, dispatcher: CoroutineDispatcher)
}
