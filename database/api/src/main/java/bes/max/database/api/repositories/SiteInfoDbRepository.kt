package bes.max.database.api.repositories

import bes.max.database.api.model.SiteInfoModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

interface SiteInfoDbRepository {

    fun getAll(dispatcher: CoroutineDispatcher): Flow<List<SiteInfoModel>>

    suspend fun getByName(name: String, dispatcher: CoroutineDispatcher): SiteInfoModel?

    suspend fun insertAll(models: List<SiteInfoModel>, dispatcher: CoroutineDispatcher)

    suspend fun update(model: SiteInfoModel, dispatcher: CoroutineDispatcher): Boolean

    suspend fun insert(model: SiteInfoModel, dispatcher: CoroutineDispatcher)


}