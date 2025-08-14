package com.example.climapp.domain.model

import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("data") val data: List<City>
)

data class City(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String?,
    @SerializedName("city") val city: String,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
    @SerializedName("countryCode") val countryCode: String,
    @SerializedName("region") val region: String?,
    @SerializedName("regionCode") val regionCode: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)

data class defaultCity(
@SerializedName("id") val id: Int = 0,
@SerializedName("type") val type: String? = "",
@SerializedName("city") val city: String = "",
@SerializedName("name") val name: String = "",
@SerializedName("country") val country: String = "",
@SerializedName("countryCode") val countryCode: String = "",
@SerializedName("region") val region: String? = "",
@SerializedName("regionCode") val regionCode: String? = "",
@SerializedName("latitude") val latitude: Double = 0.0,
@SerializedName("longitude") val longitude: Double = 0.0,
)
