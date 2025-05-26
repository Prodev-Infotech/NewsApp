import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.serialization)

    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
}


kotlin {
    androidTarget()
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    jvm()
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here

            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.uuid)
            implementation(libs.kotlinx.datetime)


            implementation(libs.ktorfit.lib)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            api(libs.mvvm.core) // only ViewModel, EventsDispatcher, Dispatchers.UI
            api(libs.moko.mvvm.compose) // api mvvm-core, getViewModel for Compose Multiplatfrom
            implementation(libs.androidx.ui)

            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("org.jetbrains.compose.ui:ui:1.5.0") // Compose Multiplatform UI

        }
        androidMain.dependencies{
            implementation ("androidx.core:core-ktx:1.10.0")

            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.runtime)
            implementation(libs.androidx.ui)
            implementation(libs.lifecycle.runtime.ktx)

            implementation(libs.google.accompanist.permissions)
//            implementation(libs.activity.compose.v182)
            implementation("androidx.fragment:fragment-ktx:1.8.7")
        }

        wasmJsMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.8.1")
            implementation("org.jetbrains.compose.web:web-core:1.8.1")
            implementation("org.jetbrains.compose.web:web-widgets:1.8.1")
//            implementation("androidx.compose.ui:ui-graphics-android:1.8.2")
            implementation("org.jetbrains.compose.ui:ui:1.5.0")
        }
        iosMain.dependencies {
                implementation("org.jetbrains.compose.ui:ui:1.5.0")
            implementation("androidx.compose.foundation:foundation-android:1.8.2")
        }

//        commonMain.dependencies {
//            implementation(libs.ktor.serialization.kotlinx.json)
//            implementation(libs.kotlinx.serialization.json)
//            implementation(libs.uuid)
//            implementation(libs.kotlinx.datetime)
//
//            implementation(libs.ktorfit.lib)
//            implementation(libs.ktor.client.core)
//            implementation(libs.ktor.client.content.negotiation)
//            api(libs.mvvm.core) // multiplatform ViewModel and related core
//            api(libs.moko.mvvm.compose) // multiplatform MVVM for Compose
//            // Remove Android-specific libraries from here!
//
//            implementation("com.russhwolf:multiplatform-settings:1.3.0")
//            implementation("org.jetbrains.compose.ui:ui:1.5.0") // Compose Multiplatform UI
//        }
//
//        androidMain.dependencies {
//            implementation("androidx.core:core-ktx:1.10.0")
//            implementation(libs.androidx.ui)
//            implementation(libs.google.accompanist.permissions)
//            implementation(libs.activity.compose.v182)
//        }
//
//        wasmJsMain.dependencies {
//            implementation("org.jetbrains.compose.runtime:runtime:1.8.1")
//            implementation("org.jetbrains.compose.web:web-core:1.8.1")
//            implementation("org.jetbrains.compose.web:web-widgets:1.8.1")
//            implementation("org.jetbrains.compose.ui:ui:1.5.0")  // Match Compose UI version if possible
//        }
//
//        iosMain.dependencies {
//            implementation("org.jetbrains.compose.ui:ui:1.5.0")
//        }
    }
}

android {
    namespace = "org.kotlin.multiplatform.newsapp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
dependencies {
//    implementation(libs.androidx.ui.graphics.android)
}
