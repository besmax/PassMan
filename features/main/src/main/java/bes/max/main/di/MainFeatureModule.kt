package bes.max.main.di

import bes.max.database.api.repositories.SiteInfoDbRepository
import bes.max.main.data.SiteInfoRepositoryImpl
import bes.max.main.domain.repositories.SiteInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainFeatureModule {

    @Provides
    @Singleton
    fun provideSiteInfoRepository(siteInfoDbRepository: SiteInfoDbRepository): SiteInfoRepository =
        SiteInfoRepositoryImpl(siteInfoDbRepository)


}