# Climapp
Este es un proyecto de una app de clima para Android, desarrollado 100% en Kotlin y Jetpack Compose. El objetivo principal es usar tecnologías modernas de Android y armar algo sólido para mi crecimiento como desarrollador.

La app permite a los usuarios buscar ciudades y agregarlas a una lista principal. Esta lista muestra el clima actual de cada ciudad guardada, funcionando como un dashboard.

## Funcionalidades
### Pantalla Principal: Muestra una lista (LazyColumn) con tarjetas de clima de todas las ciudades guardadas en la base de datos local.

- Añadir Ciudad: Una pantalla de búsqueda que consume la API de GeoDB para encontrar ciudades. Al seleccionar una, se guarda en Room.

- Gestión de Ciudades: Al mantener presionada una tarjeta de clima en la pantalla principal, aparece un diálogo para confirmar su eliminación.

- Mapa del Clima: Una pantalla con MapLibre que muestra marcadores con la temperatura actual en diferentes capitales.

## Ajustes:

- Cambio de tema (Claro, Oscuro o automático del Sistema) usando DataStore.

- Cambio de unidad de temperatura (Celsius / Fahrenheit).

## Stack tecnológico
- Este proyecto está construido con una arquitectura MVVM y un enfoque (casi) offline-first.

- Lenguaje: Kotlin

- UI: Jetpack Compose

- Arquitectura: MVVM (ViewModels, Repositorio, Fuentes de Datos)

- Asincronía: Coroutines y Flow (especialmente StateFlow para la UI)

- Inyección de Dependencias: Hilt

- Networking: Retrofit para consumir APIs REST.

- BBDD Local: Room para guardar las ciudades.

- Preferencias: DataStore para guardar los ajustes del usuario.

- Mapas: MapLibre

## Manejo de APIs:

- OpenWeatherMap (para los datos del clima).

- Geo-DB (RapidAPI) para la búsqueda de ciudades.

- MapTiler para estética de mapa.

## Cómo ejecuto el proyecto
Para compilar y ejecutar la app, necesitas tus propias claves de API para los servicios que utiliza.

1. Cloná el repositorio.

2. En la carpeta raíz del proyecto (al mismo nivel que local.properties), creá un archivo llamado juan.properties.

3. Abrí ese archivo y agrega tus claves con el siguiente formato:

### Archivo properties

- API_KEY= "tu key de open weather map"
- RAPID_API_KEY= "tu key de rapid API para geo db"
- MAP_KEY= "tu key de maptiler"

4. Sincronizá el proyecto con Gradle y ejecutalo.

## (Queda pendiente)

- Implementar la lógica de edición en el diálogo de Gestionar Ciudad.

- Aplicar la unidad de temperatura (C°/F°) desde DataStore a los datos del clima.

- Mejorar la UI del mapa y no mostrar temperaturas solamente de las capitales de las provincias argentinas.


Si llegaste hasta acá, gracias!
