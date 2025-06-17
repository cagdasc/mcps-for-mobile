plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ai.koog)
            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.android.tools.ddms)
            }
        }
    }
}
