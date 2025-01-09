package bes.max.passman.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import bes.max.features.main.data.CategoriesRepositoryImpl
import bes.max.features.main.data.SiteInfoRepositoryImpl
import bes.max.features.main.domain.repositories.CategoriesRepository
import bes.max.features.main.domain.repositories.SiteInfoRepository
import bes.max.passman.cipher.CipherImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "appdatabase.db"

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
    fun provideFileWriter(@ApplicationContext context: Context): FileWriter =
        FileWriterImpl(context)

    @Provides
    @Singleton
    fun provideFileReader(@ApplicationContext context: Context): FileReader =
        FileReaderImpl(context)

    @Provides
    @Singleton
    fun provideFileExportRepository(
        fileReader: FileReader,
        fileWriter: FileWriter,
        categoryDbRepository: CategoryDbRepository,
        siteInfoDbRepository: SiteInfoDbRepository,
    ): FileExportRepository = FileExportRepositoryImpl(
        fileReader,
        fileWriter,
        categoryDbRepository,
        siteInfoDbRepository,
    )


}