package bes.max.export.data

import android.net.Uri
import bes.max.cipher.api.CipherApi
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
    private val cipher: CipherApi,
) : FileExportRepository {

    override suspend fun export(dispatcher: CoroutineDispatcher): String =
        withContext(dispatcher) {
            val map = buildMap<String, List<Any>> {
                val categories = categoryDbRepository.getAll(dispatcher).firstOrNull()
                var header = CategoryModel.Companion::class.java.name
                if (categories != null) put(header, categories as List<Any>)

                val siteInfoModels =
                    decryptPasswords(siteInfoDbRepository.getAll(dispatcher).firstOrNull())
                header = SiteInfoModel.Companion::class.java.name
                if (siteInfoModels.isNotEmpty()) put(header, siteInfoModels as List<Any>)
            }
            return@withContext fileWriter.writeData(map)
        }


    override suspend fun import(uri: Uri, code: String, dispatcher: CoroutineDispatcher) {
        withContext(dispatcher) {
            fileReader.readData(uri, code).forEach { dataEntry ->
                when (dataEntry.key) {
                    SiteInfoModel.Companion::class.java.name -> {
                        val list = (dataEntry.value as List<SiteInfoModel>).map { model ->
                            val haveSimilar = findSimilar(model, dispatcher)
                            val encrypted = cipher.encrypt(
                                if (haveSimilar) model.name + "_import" else model.name,
                                model.password
                            )
                            model.copy(
                                id = -1,
                                name = if (haveSimilar) model.name + "_import" else model.name,
                                password = encrypted.encryptedData,
                                passwordIv = encrypted.passwordIv,
                            )
                        }
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

    private suspend fun decryptPasswords(list: List<SiteInfoModel>?): List<SiteInfoModel> {
        if (list == null) return emptyList()

        return list.map { it.copy(password = cipher.decrypt(it.name, it.password, it.passwordIv)) }
    }

    private suspend fun findSimilar(model: SiteInfoModel, dispatcher: CoroutineDispatcher): Boolean {
        val haveSameUrl = siteInfoDbRepository.getByUrl(model.url, dispatcher).isNotEmpty()
        if (haveSameUrl) return true
        val haveSameName = siteInfoDbRepository.getByName(model.name, dispatcher) != null
        return haveSameName
    }

}