import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

group = "com.freetime"
version = "2.0.0"

kotlin {
    android {
        namespace = "com.freetime.sdk"
        compileSdk = 37
        minSdk = 26

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
            implementation("io.ktor:ktor-client-core:3.5.2")
            implementation("io.ktor:ktor-client-content-negotiation:3.5.2")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.5.2")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
        }

        jvmMain.dependencies {
            implementation("io.github.g0dkar:qrcode-kotlin:4.5.0")
            implementation("io.ktor:ktor-client-okhttp:3.5.2")
        }
    }
}
