package com.example.climapp.data.models


//Ciudades hardcodeadas para prueba funcional


data class City(val name: String, val lat: String, val lon: String)

val cities = listOf(
    City("Buenos Aires", "-34.6037", "-58.3816"),
    City("Córdoba", "-31.4201", "-64.1888"),
    City("Rosario", "-32.9575", "-60.6394"),
    City("Mendoza", "-32.8908", "-68.8272"),
    City("San Miguel de Tucumán", "-26.8083", "-65.2176"),
    City("La Plata", "-34.9214", "-57.9544"),
    City("Mar del Plata", "-38.0055", "-57.5426"),
    City("Salta", "-24.7821", "-65.4232"),
    City("Santa Fe", "-31.6333", "-60.7000"),
    City("San Juan", "-31.5375", "-68.5364"),
    City("Resistencia", "-27.4516", "-58.9867"),
    City("Santiago del Estero", "-27.7951", "-64.2615"),
    City("Corrientes", "-27.4806", "-58.8341"),
    City("Posadas", "-27.3622", "-55.9009"),
    City("Neuquén", "-38.9516", "-68.0591")
)