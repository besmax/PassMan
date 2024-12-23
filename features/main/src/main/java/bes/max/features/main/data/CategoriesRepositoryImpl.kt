package bes.max.features.main.data

import bes.max.database.api.repositories.CategoryDbRepository
import bes.max.features.main.domain.models.CategoryModelMain
import bes.max.features.main.domain.repositories.CategoriesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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
}