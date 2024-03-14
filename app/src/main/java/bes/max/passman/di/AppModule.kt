package bes.max.passman.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import bes.max.cipher.api.CipherApi
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.database.impl.AppDatabase
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.repositories.SiteInfoDbRepositoryImpl
import bes.max.features.main.data.SiteInfoRepositoryImpl
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
    fun provideSiteInfoDbRepository(dao: SiteInfoDao): SiteInfoDbRepository =
        SiteInfoDbRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideSiteInfoRepository(siteInfoDbRepository: SiteInfoDbRepository): SiteInfoRepository =
        SiteInfoRepositoryImpl(siteInfoDbRepository)

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
            .fallbackToDestructiveMigration()
            .build()
    }
}