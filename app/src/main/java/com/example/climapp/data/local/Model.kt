package com.example.climapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey val id: Int, // refactor para evitar duplicado
    val name: String,
    val lat: Double,
    val lon: Double
)