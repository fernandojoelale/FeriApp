import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// URL del backend. Por defecto: emulador (10.0.2.2 = tu PC). Para un celular real o
// otra red, agregar en local.properties (que NO se sube a git):  api.baseUrl=http://192.168.0.58:8080/
val apiBaseUrl: String = Properties().let { props ->
    val archivo = rootProject.file("local.properties")
    if (archivo.exists()) archivo.inputStream().use { props.load(it) }
    props.getProperty("api.baseUrl") ?: "http://10.0.2.2:8080/"
}

android {
    namespace = "edu.uadeapps.feriapp"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "edu.uadeapps.feriapp"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.compose.material:material-icons-extended") //iconos
    implementation("androidx.navigation:navigation-compose:2.7.7") //navegacion entre activities
    implementation("com.squareup.retrofit2:retrofit:2.9.0") //retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // convertidor de JSON a Kotlin
    implementation("androidx.datastore:datastore-preferences:1.1.7") // sesion (token, rol) en el dispositivo
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}