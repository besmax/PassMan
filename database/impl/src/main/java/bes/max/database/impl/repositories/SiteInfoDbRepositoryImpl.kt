package bes.max.database.impl.repositories

import bes.max.database.api.model.SiteInfoModel
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.mappers.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SiteInfoDbRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val siteInfoDao: SiteInfoDao,
) : SiteInfoDbRepository {
    override fun getAll(): Flow<List<SiteInfoModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getByName(name: String): SiteInfoModel {
        return withContext(dispatcher) {
            siteInfoDao.getByName(name).map()
        }
    }

    override suspend fun insertAll(models: List<SiteInfoModel>) {
        withContext(dispatcher) {
            siteInfoDao.insertAll(models.map { it.map() })
        }
    }

    override suspend fun update(model: SiteInfoModel) {
        withContext(dispatcher) {
            siteInfoDao.update(model.map())
        }
    }
}