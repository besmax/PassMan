package bes.max.main.data

import bes.max.database.api.model.SiteInfoModel
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.main.domain.repositories.SiteInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SiteInfoRepositoryImpl(
    private val siteInfoDbRepository: SiteInfoDbRepository
): SiteInfoRepository {
    override fun getAll(): Flow<List<SiteInfoModel>> = siteInfoDbRepository.getAll(Dispatchers.IO)
    override suspend fun getById(id: Int): SiteInfoModel? = siteInfoDbRepository.getById(id, Dispatchers.IO)

    override suspend fun create(model: SiteInfoModel) {
        siteInfoDbRepository.insert(model, Dispatchers.IO)
    }

    override suspend fun update(model: SiteInfoModel) {
        siteInfoDbRepository.update(model, Dispatchers.IO)
    }

    override suspend fun delete(model: SiteInfoModel) {
        siteInfoDbRepository.delete(model, Dispatchers.IO)
    }
}