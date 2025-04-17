package bes.max.passman.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import bes.max.cipher.api.CipherApi
import bes.max.database.api.repositories.CategoryDbRepository
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.database.impl.AppDatabase
import bes.max.database.impl.dao.CategoryDao
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.migration.MIGRATION_1_2
import bes.max.database.impl.repositories.CategoryDbRepositoryImpl
import bes.max.database.impl.repositories.SiteInfoDbRepositoryImpl
import bes.max.export.data.FileExportRepositoryImpl
import bes.max.export.data.FileReaderImpl
import bes.max.export.data.FileWriterImpl
import bes.max.export.domain.FileExportRepository
import bes.max.export.domain.FileReader
import bes.max.export.domain.FileWriter
import bes.max.features.main.data.datastore.PinCodeSerializer
import bes.max.features.main.data.repo.CategoriesRepositoryImpl
import bes.max.features.main.data.repo.SettingsRepositoryImpl
import bes.max.features.main.data.repo.SiteInfoRepositoryImpl
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SettingsRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import bes.max.features.main.proto.PinCodeModel
import bes.max.passman.cipher.CipherImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

private const val DATABASE_NAME = "appdatabase.db"
private const val SETTINGS_PREFERENCES = "settings_preferences"
private const val PIN_CODE_DATA_STORE_FILE_NAME = "pin_code_datastore.pb"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_PREFERENCES
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSiteInfoDao(database: AppDatabase): SiteInfoDao =
        database.siteInfoDao()

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao =
        database.categoryDao()

    @Provides
    @Singleton
    fun provideSiteInfoDbRepository(dao: SiteInfoDao): SiteInfoDbRepository =
        SiteInfoDbRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryDbRepository(dao: CategoryDao): CategoryDbRepository =
        CategoryDbRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideSiteInfoRepository(siteInfoDbRepository: SiteInfoDbRepository): SiteInfoRepository =
        SiteInfoRepositoryImpl(siteInfoDbRepository)

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoryDbRepository: CategoryDbRepository): CategoriesRepository =
        CategoriesRepositoryImpl(categoryDbRepository)

    @RequiresApi(Build.VERSION_CODES.R)
    @Provides
    @Singleton
    fun provideCipherApi(): CipherApi {
        return CipherImpl
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideFileWriter(
        @ApplicationContext context: Context,
        cipherApi: CipherApi,
    ): FileWriter =
        FileWriterImpl(context, cipherApi)

    @Provides
    @Singleton
    fun provideFileReader(
        @ApplicationContext context: Context,
        cipherApi: CipherApi,
    ): FileReader =
        FileReaderImpl(context, cipherApi)

    @Provides
    @Singleton
    fun provideFileExportRepository(
        fileReader: FileReader,
        fileWriter: FileWriter,
        categoryDbRepository: CategoryDbRepository,
        siteInfoDbRepository: SiteInfoDbRepository,
        cipherApi: CipherApi,
    ): FileExportRepository = FileExportRepositoryImpl(
        fileReader,
        fileWriter,
        categoryDbRepository,
        siteInfoDbRepository,
        cipherApi
    )

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext appContext: Context) =
        appContext.dataStore

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext appContext: Context,
        dataStore: DataStore<Preferences>,
        pinCodeDataStore: DataStore<PinCodeModel>
    ): SettingsRepository {
        return SettingsRepositoryImpl(appContext, dataStore, pinCodeDataStore)
    }

    @Singleton
    @Provides
    fun providePinCode(@ApplicationContext context: Context): DataStore<PinCodeModel> {
        return DataStoreFactory.create(
            serializer = PinCodeSerializer,
            produceFile = { context.dataStoreFile(PIN_CODE_DATA_STORE_FILE_NAME) },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}
