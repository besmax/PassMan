package bes.max.database.api.repositories

import bes.max.database.api.model.SiteInfoModel
import kotlinx.coroutines.flow.Flow

interface SiteInfoDbRepository {

    fun getAll(): Flow<List<SiteInfoModel>>

    suspend fun getByName(name: String): SiteInfoModel

    suspend fun insertAll(models: List<SiteInfoModel>)

    suspend fun update(model: SiteInfoModel)

}