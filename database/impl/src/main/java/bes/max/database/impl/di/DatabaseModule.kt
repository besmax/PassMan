package bes.max.database.impl.di
//
//import android.content.Context
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import androidx.sqlite.db.SupportSQLiteDatabase
//import bes.max.database.api.repositories.SiteInfoDbRepository
//import bes.max.database.impl.AppDatabase
//import bes.max.database.impl.dao.SiteInfoDao
//import bes.max.database.impl.repositories.SiteInfoDbRepositoryImpl
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.SupervisorJob
//import kotlinx.coroutines.launch
//import javax.inject.Singleton
//
//private const val DATABASE_NAME = "appdatabase.db"
//
//@Module
//@InstallIn(SingletonComponent::class)
//class DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
//        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
//            //insert mock data in db
//            .addCallback(object : RoomDatabase.Callback() {
//                @RequiresApi(Build.VERSION_CODES.M)
//                override fun onCreate(db: SupportSQLiteDatabase) {
//                    super.onCreate(db)
//                    CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
//                        provideDatabase(context).siteInfoDao().insertAll(MockData.list)
//
//                    }
//                }
//            })
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideSiteInfoDao(database: AppDatabase): SiteInfoDao =
//        database.siteInfoDao()
//
//    @Provides
//    @Singleton
//    fun provideSiteInfoDbRepository(dao: SiteInfoDao): SiteInfoDbRepository =
//        SiteInfoDbRepositoryImpl(dao)
//}