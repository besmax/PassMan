package bes.max.features.main.domain.repositories

import bes.max.database.api.model.SiteInfoModel
import kotlinx.coroutines.flow.Flow

interface SiteInfoRepository {

    fun getAll(): Flow<List<SiteInfoModel>>

    suspend fun getById(id: Int): SiteInfoModel?

    suspend fun create(model: SiteInfoModel)

    suspend fun update(model: SiteInfoModel)

    suspend fun delete(model: SiteInfoModel)

}