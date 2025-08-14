package com.example.climapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: CityEntity)

    @Query("SELECT * FROM cities ORDER BY id DESC LIMIT 5")
    fun getRecentCities(): Flow<List<CityEntity>>
}