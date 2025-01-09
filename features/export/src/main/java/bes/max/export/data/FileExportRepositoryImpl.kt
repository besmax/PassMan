package bes.max.export.data

import android.net.Uri
import bes.max.database.api.model.CategoryModel
import bes.max.database.api.model.SiteInfoModel
import bes.max.database.api.repositories.CategoryDbRepository
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.export.domain.FileExportRepository
import bes.max.export.domain.FileReader
import bes.max.export.domain.FileWriter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class FileExportRepositoryImpl(
    private val fileReader: FileReader,
    private val fileWriter: FileWriter,
    private val categoryDbRepository: CategoryDbRepository,
    private val siteInfoDbRepository: SiteInfoDbRepository,
) : FileExportRepository {

    override suspend fun export(dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            val map = buildMap<String, List<Any>> {
                val categories = categoryDbRepository.getAll(dispatcher).firstOrNull()
                var header = CategoryModel.Companion::class.java.name
                if (categories != null) put(header, categories as List<Any>)

                val siteInfoModels = siteInfoDbRepository.getAll(dispatcher).firstOrNull()
                header = SiteInfoModel.Companion::class.java.name
                if (siteInfoModels != null) put(header, siteInfoModels as List<Any>)
            }
            fileWriter.writeData(map)
        }
    }

    override suspend fun import(uri: Uri, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            fileReader.readData(uri).forEach { dataEntry ->
                when (dataEntry.key) {
                    SiteInfoModel.Companion::class.java.name -> {
                        val list = dataEntry.value as List<SiteInfoModel>
                        siteInfoDbRepository.insertAll(list, dispatcher)
                    }

                    CategoryModel.Companion::class.java.name -> {
                        val list = dataEntry.value as List<CategoryModel>
                        categoryDbRepository.insertAll(list, dispatcher)
                    }

                    else -> {
                        error("Unknown model")
                    }
                }
            }
        }
    }

}