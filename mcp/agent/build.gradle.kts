plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":mcp:adb"))
            implementation(project(":mcp:domain"))
            implementation(libs.ai.koog)
        }
    }
}
