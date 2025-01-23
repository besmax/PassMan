package bes.max.features.main.data.repo

import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.features.main.data.converter.map
import bes.max.features.main.domain.models.SiteInfoModelMain
import bes.max.features.main.domain.repositories.SiteInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SiteInfoRepositoryImpl(
    private val siteInfoDbRepository: SiteInfoDbRepository
) : SiteInfoRepository {
    override fun getAll(): Flow<List<SiteInfoModelMain>> =
        siteInfoDbRepository.getAll(Dispatchers.IO).map { list -> list.map { it.map() } }

    override suspend fun getById(id: Int): SiteInfoModelMain? =
        siteInfoDbRepository.getById(id, Dispatchers.IO)?.map()

    override suspend fun getByCategory(category: Int): List<SiteInfoModelMain> =
        siteInfoDbRepository.getByCategory(category, Dispatchers.IO).map { it.map() }


    override suspend fun create(model: SiteInfoModelMain) {
        siteInfoDbRepository.insert(model.map(), Dispatchers.IO)
    }

    override suspend fun update(model: SiteInfoModelMain) {
        siteInfoDbRepository.update(model.map(), Dispatchers.IO)
    }

    override suspend fun delete(model: SiteInfoModelMain) {
        siteInfoDbRepository.delete(model.map(), Dispatchers.IO)
    }

    override suspend fun isNotEmpty(): Boolean {
       return siteInfoDbRepository.isNotEmpty()
    }
}
