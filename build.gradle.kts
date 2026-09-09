import org.gradle.kotlin.dsl.apply

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.kotlin.multiplatform.library") version "9.4.0" apply false
    id("org.jetbrains.kotlin.multiplatform") version "2.4.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.20" apply false
}