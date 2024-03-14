package bes.max.features.main.domain.repositories

import bes.max.features.main.domain.models.SiteInfoModelMain
import kotlinx.coroutines.flow.Flow

interface SiteInfoRepository {

    fun getAll(): Flow<List<SiteInfoModelMain>>

    suspend fun getById(id: Int): SiteInfoModelMain?

    suspend fun create(model: SiteInfoModelMain)

    suspend fun update(model: SiteInfoModelMain)

    suspend fun delete(model: SiteInfoModelMain)

}