package bes.max.features.main.data

import bes.max.database.api.repositories.CategoryDbRepository
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.models.FilterModel
import bes.max.features.main.domain.repositories.CategoriesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class CategoriesRepositoryImpl(
    private val categoryDbRepository: CategoryDbRepository
): CategoriesRepository {
    override fun getAll(dispatcher: CoroutineDispatcher): Flow<List<CategoryModelMain>> {
        return categoryDbRepository.getAll(dispatcher).map { list -> list.map { it.map() } }
    }

    override suspend fun getByName(
        name: String,
        dispatcher: CoroutineDispatcher
    ): CategoryModelMain? {
        return categoryDbRepository.getByName(name, dispatcher)?.map()
    }

    override suspend fun getById(id: Int, dispatcher: CoroutineDispatcher): CategoryModelMain? {
        return categoryDbRepository.getById(id, dispatcher)?.map()
    }

    override suspend fun insertAll(
        models: List<CategoryModelMain>,
        dispatcher: CoroutineDispatcher
    ) {
        categoryDbRepository.insertAll(models.map { it.map() },dispatcher)
    }

    override suspend fun update(model: CategoryModelMain, dispatcher: CoroutineDispatcher) {
        categoryDbRepository.update(model.map(), dispatcher)
    }

    override suspend fun insert(model: CategoryModelMain, dispatcher: CoroutineDispatcher) {
        categoryDbRepository.insert(model.map(), dispatcher)
    }

    override suspend fun delete(model: CategoryModelMain, dispatcher: CoroutineDispatcher) {
        categoryDbRepository.delete(model.map(), dispatcher)
    }

    override suspend fun deleteByColor(color: Int, dispatcher: CoroutineDispatcher) {
        categoryDbRepository.deleteByColor(color, dispatcher)
    }

    override suspend fun getFilters(filterAction: (Int) -> Unit, dispatcher: CoroutineDispatcher): List<FilterModel> {
        val categories = getAll(dispatcher).flowOn(dispatcher).firstOrNull()
        return categories?.map { it.toFilter(filterAction) } ?: emptyList()
    }
}