import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.kotlinx.html)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.ktor.common)
            implementation(compose.materialIconsExtended)
            implementation(libs.androidx.lifecycle.viewmodel.compose)

            implementation(libs.ksoup)
            implementation(libs.russhwolf.multiplatform.settings)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.ktor.client.cio)
            implementation(libs.bundles.openhtmltopdf.common)
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.chaosdave34.benzol.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.AppImage, TargetFormat.Rpm)
            packageName = rootProject.name
            packageVersion = "1.3.0"
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
            configurationFiles.from("proguard.pro")
        }
    }
}