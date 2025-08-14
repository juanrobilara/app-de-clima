import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.climapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.climapp"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        val keystoreFile = project.rootProject.file( "juan.properties" )
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //devolver una clave vacía en caso de que algo salga mal
        val apiKey = properties.getProperty( "API_KEY" ) ?: ""
        val rapidKey = properties.getProperty("RAPID_API_KEY") ?: ""
        val mapKey = properties.getProperty("MAP_KEY") ?: ""

        buildConfigField(
            type = "String" ,
            name = "API_KEY" ,
            value = apiKey
        )

        buildConfigField(
            type = "String",
            name = "RAPID_API_KEY",
            value = rapidKey
        )

        buildConfigField(
            type = "String",
            name = "MAP_KEY",
            value = mapKey
        )

    }

    buildTypes {


        release {

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
        kotlinOptions {
            jvmTarget = "11"
        }
        buildFeatures {
            compose = true
            buildConfig = true
        }


    }



    dependencies {

        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
        implementation("io.coil-kt:coil-compose:2.4.0")

        val hilt = "2.55"
        implementation("com.google.dagger:hilt-android:$hilt")
        //ksp("com.google.dagger:hilt-compiler:$hilt")
        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.room.ktx)
        implementation(libs.room.runtime)
        ksp(libs.room.compiler)
        implementation(libs.androidx.runtime.livedata)
        ksp("com.google.dagger:hilt-android-compiler:$hilt")
        implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
        testImplementation("androidx.arch.core:core-testing:2.2.0")
        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
        testImplementation("com.google.dagger:hilt-android-testing:2.48")
        kspTest("com.google.dagger:hilt-android-compiler:2.48")
        androidTestImplementation("com.google.dagger:hilt-android-testing:$hilt")
        kspAndroidTest("com.google.dagger:hilt-android-compiler:$hilt")

        implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

        implementation(libs.weather.icons)
        implementation(libs.iconics.core)
        implementation ("androidx.compose.material:material-icons-extended")

        implementation ("org.maplibre.gl:android-sdk:11.8.0")
        implementation ("org.maplibre.gl:android-plugin-annotation-v9:3.0.2")
        implementation ("com.google.accompanist:accompanist-permissions:0.30.1")
        implementation ("com.google.android.gms:play-services-location:21.0.1")


        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)

    }
}