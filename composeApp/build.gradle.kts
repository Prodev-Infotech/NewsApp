import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    jvm()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    
    sourceSets {
//        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(projects.shared)

            implementation(libs.androidx.lifecycle.viewmodel.compose)
            // Navigation
            implementation(libs.androidx.navigation.compose)

            // Material 3
            implementation(compose.material3)
            // window-size
            implementation(libs.screen.size)
            implementation(libs.media.kamel.image.default)
            implementation(libs.kotlin.reflect)
            implementation(libs.kamel.decoder.image.bitmap)

//            implementation("io.coil-kt:coil:2.6.0")

            implementation("io.github.vinceglb:filekit-core:0.10.0-beta04")
            implementation("io.github.vinceglb:filekit-coil:0.10.0-beta04")

            // Enables FileKit dialogs without Compose dependencies
            implementation("io.github.vinceglb:filekit-dialogs:0.10.0-beta04")

// Enables FileKit dialogs with Composable utilities
            implementation("io.github.vinceglb:filekit-dialogs-compose:0.10.0-beta04")
//            implementation("io.github.kevinnzou:compose-multiplatform-camera:0.3.0")

            implementation("media.kamel:kamel-image:1.0.5") // Alternative

        }
//        desktopMain.dependencies {
//            implementation(compose.desktop.currentOs)
//            implementation(libs.kotlinx.coroutines.swing)
//        }
    }

//    sourceSets {
//        val desktopMain by getting
//        val androidMain by getting
//        val commonMain by getting
//
//        androidMain.dependencies {
//            implementation(compose.preview)
//            implementation(libs.androidx.activity.compose)
//            implementation(libs.androidx.lifecycle.viewmodel)
//            implementation(libs.androidx.navigation.compose)
//        }
//
//        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material)
//            implementation(compose.ui)
//            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)
//
//            // Remove Android-only libraries from here!
//
//            // Only add multiplatform safe libs here:
//            implementation(compose.material3)
//            implementation(libs.screen.size)
//            implementation(libs.media.kamel.image.default)
//            implementation(libs.kotlin.reflect)
//            implementation(libs.kamel.decoder.image.bitmap)
//
//            // Be sure projects.shared is not circular here
//            implementation(projects.shared)
//        }
//
//        desktopMain.dependencies {
//            implementation(compose.desktop.currentOs)
//            implementation(libs.kotlinx.coroutines.swing)
//        }
//    }
}

android {
    namespace = "org.kotlin.multiplatform.newsapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.kotlin.multiplatform.newsapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get() // This is the line that should now resolve
    }

}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.kotlin.multiplatform.newsapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.kotlin.multiplatform.newsapp"
            packageVersion = "1.0.0"
        }
    }
}
