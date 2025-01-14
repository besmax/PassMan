package bes.max.features.main.domain.repositories

import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.models.FilterModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface CategoriesRepository {
    fun getAll(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<List<CategoryModelMain>>

    suspend fun getByName(
        name: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): CategoryModelMain?

    suspend fun getById(
        id: Int,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): CategoryModelMain?

    suspend fun insertAll(
        models: List<CategoryModelMain>,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    )

    suspend fun update(model: CategoryModelMain, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun insert(model: CategoryModelMain, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun delete(model: CategoryModelMain, dispatcher: CoroutineDispatcher = Dispatchers.IO)

    suspend fun deleteByColor(color: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO)


    suspend fun getFilters(filterAction: (Int) -> Unit, dispatcher: CoroutineDispatcher = Dispatchers.IO): List<FilterModel>
}
