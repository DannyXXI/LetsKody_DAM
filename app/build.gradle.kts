plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.4"   // plugin ksp (agregar primero y sincronizar)
    alias(libs.plugins.google.gms.google.services)  // plugin para usar Firebase
}

android {
    namespace = "com.juandeherrera.letskody"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.juandeherrera.letskody"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // librerías para Material3
    implementation("androidx.compose.material3:material3:1.4.0") // dependencia de actualización

    // librerías para iconos
    implementation("androidx.compose.material:material-icons-extended:1.5.0-alpha11") // dependencia para iconos de formularios
    implementation("com.composables:icons-lucide:1.1.0")                              // dependencia para la librería de iconos Lucide

    // librerías para usar la base de datos local (Room - SQLite)
    implementation("androidx.room:room-runtime:2.8.4") // dependencia principal para poder usar Room
    ksp("androidx.room:room-compiler:2.8.4")           // dependencia que permite usar el procesador de anotaciones de Room (KSP)
    implementation("androidx.room:room-ktx:2.8.4")     // dependencia que agrega extensiones de Kotlin para Room
    implementation("io.coil-kt:coil-compose:2.7.0")    // dependencia para usar imágenes desde Internet (AsyncImage)

    // librerías para el uso de notificaciones
    implementation(platform("androidx.compose:compose-bom:2025.12.00")) // dependencia para usar notificaciones en el Snackbar
    implementation("com.google.accompanist:accompanist-permissions:0.31.1-alpha") // dependencia para usar notificaciones con icono

    // librerías para usar Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))  // dependencia para usar el boom de Firebase
    implementation("com.google.firebase:firebase-auth-ktx")                        // dependencia para usar la autenticación de Firebase
    implementation(libs.firebase.firestore)                                        // dependencia para usar la base de datos de Firebase

    // librerías para la navegación entre pantallas
    implementation(libs.androidx.navigation.compose)  // dependencia para utilizar la navegación entre pantallas
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.5.0-alpha11") // dependencia para la navegación adaptativa según el tamaño de pantalla
    implementation("androidx.compose.material3:material3-window-size-class:1.4.0")                 // dependencia para usar herramientas para clasificar el tamaño de la pantalla

    // librerías para corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")  // dependencia que conecta los servicios de Google Play con las corrutinas de kotlin

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}