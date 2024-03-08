package bes.max.database.impl.di

import android.content.Context
import androidx.room.Room
import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.database.impl.AppDatabase
import bes.max.database.impl.dao.SiteInfoDao
import bes.max.database.impl.repositories.SiteInfoDbRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "appdatabase.db"

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideSiteInfoDao(database: AppDatabase): SiteInfoDao =
        database.siteInfoDao()

    @Provides
    @Singleton
    fun provideSiteInfoDbRepository(dao: SiteInfoDao): SiteInfoDbRepository =
        SiteInfoDbRepositoryImpl(dao)
}