package com.example.climapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.climapp.data.local.AppDatabase
import com.example.climapp.data.local.CityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_app_db"
        ).build()
    }

    @Provides
    fun provideCityDao(db: AppDatabase): CityDao = db.cityDao()
}

