package com.example.climapp.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/*

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    private const val PET_DATABASE_NAME = "pet_database"

    @Provides
    @Singleton
    fun provideRoom(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(context, PetDatabase::class.java, PET_DATABASE_NAME).build()

    @Provides
    @Singleton
    fun providePetRepository(db: PetDatabase): PetsRepository {
        return PetsRepositoryImpl(db.getPetDao())
    }
}

*/