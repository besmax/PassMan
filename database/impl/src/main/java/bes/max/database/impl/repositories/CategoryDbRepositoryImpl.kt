package bes.max.database.impl.repositories

import bes.max.database.api.model.CategoryModel
import bes.max.database.api.repositories.CategoryDbRepository
import bes.max.database.impl.dao.CategoryDao
import bes.max.database.impl.mappers.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryDbRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryDbRepository {
    override fun getAll(dispatcher: CoroutineDispatcher): Flow<List<CategoryModel>> {
        return categoryDao.getAll().map { list -> list.map { it.map() } }.flowOn(dispatcher)
    }

    override suspend fun getByName(name: String, dispatcher: CoroutineDispatcher): CategoryModel? {
        return withContext(dispatcher) { categoryDao.getByName(name)?.map() }
    }

    override suspend fun getById(id: Int, dispatcher: CoroutineDispatcher): CategoryModel? {
        return withContext(dispatcher) { categoryDao.getById(id)?.map() }
    }

    override suspend fun insertAll(models: List<CategoryModel>, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) { categoryDao.insertAll(models.map { it.map() }) }
    }

    override suspend fun update(model: CategoryModel, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) { categoryDao.update(model.map()) }
    }

    override suspend fun insert(model: CategoryModel, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) { categoryDao.insert(model.map()) }
    }

    override suspend fun delete(model: CategoryModel, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) { categoryDao.delete(model.map()) }
    }
}
