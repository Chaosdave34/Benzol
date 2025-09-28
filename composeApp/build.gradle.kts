import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)

    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-Xcontext-parameters")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            //implementation(compose.material3)
            implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha01")
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)

            implementation(libs.kotlinx.html)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.ktor.clientCommon)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.navigation)
            implementation(libs.compose.material3Adaptive)
            implementation("org.jetbrains.compose.material3:material3-adaptive-navigation-suite:1.10.0-alpha01")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.ktor.clientCio)
            implementation(libs.bundles.openhtmltopdf.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.chaosdave34.benzol.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Rpm)
            packageName = rootProject.name
            packageVersion = "2.0.0"
            linux {
                iconFile.set(project.file("src/jvmMain/resources/logo.png"))
            }
            macOS {
                iconFile.set(project.file("src/jvmMain/resources/logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/jvmMain/resources/logo.ico"))
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules.pro")
        }
    }
}