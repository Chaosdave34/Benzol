import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    js {
        browser()
        binaries.executable()
    }

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
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)

            implementation(libs.compose.material3)
            implementation(libs.kotlinx.html)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.ktor.clientCommon)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.navigation)
            implementation(libs.compose.material3Adaptive)
            implementation(libs.compose.material3AdaptiveNavigationSuite)

            implementation(libs.bundles.filekit.common)
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
            targetFormats(TargetFormat.AppImage, TargetFormat.Exe)
            fileAssociation("application/x-benzol+json", "benzol", "Benzol Betriebsanweisung")

            packageName = rootProject.name
            packageVersion = "2.2.0"
            description = "Programm zur Erstellung von Betriebsanweisungen für Laboreinheiten nach EG Nr. 1272/2008"
            licenseFile.set(rootProject.file("LICENSE"))

            linux {
                menuGroup = "Utility"
                iconFile.set(project.file("src/jvmMain/resources/logo.png"))

                modules("jdk.security.auth")
            }
//            macOS {
//                iconFile.set(project.file("src/jvmMain/resources/logo.icns"))
//            }
            windows {
                perUserInstall = true
                shortcut = true
                upgradeUuid = "2448B32F-0202-4792-A6E6-B425B4252B82"
                iconFile.set(project.file("src/jvmMain/resources/logo.ico"))
            }
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}