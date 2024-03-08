package bes.max.database.impl.repositories

import bes.max.database.api.model.SiteInfoModel
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.mappers.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SiteInfoDbRepositoryImpl(
    private val siteInfoDao: SiteInfoDao,
) : SiteInfoDbRepository {
    override fun getAll(dispatcher: CoroutineDispatcher): Flow<List<SiteInfoModel>> {
        return siteInfoDao.getAll().map { it.map { entity -> entity.map() } }
    }

    override suspend fun getByName(name: String, dispatcher: CoroutineDispatcher): SiteInfoModel {
        return withContext(dispatcher) {
            siteInfoDao.getByName(name).map()
        }
    }

    override suspend fun insertAll(models: List<SiteInfoModel>, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            siteInfoDao.insertAll(models.map { it.map() })
        }
    }

    override suspend fun update(model: SiteInfoModel, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            siteInfoDao.update(model.map())
        }
    }
}