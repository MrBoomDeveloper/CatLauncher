import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.6"
}

val keystoreProperties = Properties().apply {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if(!keystorePropertiesFile.exists()) return@apply
    load(FileInputStream(keystorePropertiesFile))
}

android {
    namespace = "com.mrboomdev.catlauncher"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mrboomdev.catlauncher"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        manifestPlaceholders["app_name"] = "CatLauncher"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String?
            keyPassword = keystoreProperties["keyPassword"] as String?
            storeFile = keystoreProperties["storeFile"]?.let { file(it as String) }
            storePassword = keystoreProperties["storePassword"] as String?
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders["app_name"] = "CatLauncherD"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        
        release {
            versionNameSuffix = "-release"
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.room3:room3-runtime:3.0.0-alpha01")
    implementation("androidx.navigation3:navigation3-runtime:1.0.1")
    implementation("androidx.navigation3:navigation3-ui:1.0.1")
    implementation("com.cheonjaeung.compose.grid:grid:2.7.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")
    implementation("io.github.dokar3:chiptextfield-m3:0.7.2-alpha01")
    implementation("com.github.idapgroup:Snowfall:0.9.10")
    ksp("androidx.room3:room3-compiler:3.0.0-alpha01")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_11
    }
}