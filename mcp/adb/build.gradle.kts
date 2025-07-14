plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(project(":shared"))

            implementation(libs.xmlutil.core)
            implementation(libs.xmlutil.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.android.tools.ddms)
                implementation(libs.android.tools.adblib)
                implementation(libs.android.tools.adblib.tools)
                implementation("com.android.tools:sdklib:31.11.1")
                implementation("com.android.tools:common:31.11.1")
                implementation("com.android.tools:sdk-common:31.11.1")
            }
        }

        val desktopTest by getting {
            dependencies {
                implementation(libs.kotlin.test.junit)
            }
        }
    }
}
