import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlin.io.path.createSymbolicLinkPointingTo

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
            implementation(libs.logback)
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
            packageVersion = "2.3.0"
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

tasks {
    register("printComposePackageInfo") {
        group = "app-image"
        val nativeDistribution = compose.desktop.application.nativeDistributions
        val packageName = nativeDistribution.packageName
        val packageVersion = nativeDistribution.packageVersion

        doLast {
            println("packageName = $packageName")
            println("packageVersion = $packageVersion")
        }
    }

    val downloadAppImageTool = project.tasks.register("downloadAppImageTool") {
        group = "distribution"

        val outputFile = project.layout.buildDirectory.file("appimagetool/appimagetool-x86_64.AppImage")

        outputs.file(outputFile)

        doLast {

            val apiUrl = "https://api.github.com/repos/AppImage/AppImageTool/releases/latest"
            val json = URI(apiUrl).toURL().readText()

            val downloadUrl = Regex(
                """"browser_download_url":\s*"([^"]*appimagetool-x86_64\.AppImage)""""
            ).find(json)?.groupValues?.get(1)
                ?: error("Could not find appimagetool in latest release")

            println("Downloading from: $downloadUrl")

            val outFile = outputFile.get().asFile
            outFile.parentFile.mkdirs()

            URI(downloadUrl).toURL().openStream().use {
                Files.copy(it, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            }

            outFile.setExecutable(true)
        }
    }

    mapOf(
        "" to "main",
        "Release" to "main-release"
    ).forEach { (name, buildDir) ->
        val cleanTask = register("clean${name}BuildDir", Delete::class.java) {
            group = "app-image"

            delete(project.layout.buildDirectory.dir("app-image/$buildDir"))
        }

        val copyTask = project.tasks.register("copy${name}ComposeAppImage", Copy::class.java) {
            group = "app-image"
            dependsOn(cleanTask, "package${name}AppImage")
            val packageName = compose.desktop.application.nativeDistributions.packageName

            into(project.layout.buildDirectory.dir("app-image/$buildDir/AppDir"))

            val baseSource = project.tasks.named("package${name}AppImage").get().outputs.files.asPath + "/$packageName"
            from(project.layout.buildDirectory.dir(baseSource)) {
                into("/usr")
            }

            from(project.layout.projectDirectory.dir("src/commonMain/composeResources/drawable/logo.svg")) {
                into("/usr/share/icons/hicolor/scalable/apps/")
                rename { "$packageName.svg" }
            }

            from(project.layout.buildDirectory.dir("$baseSource/lib/$packageName.png"))
        }

        val createAppDirTask = project.tasks.register("create${name}AppDir") {
            group = "app-image"
            dependsOn(copyTask)

            val nativeDistributions = compose.desktop.application.nativeDistributions
            val packageName = nativeDistributions.packageName
            val menuGroup = nativeDistributions.linux.menuGroup
            val description = nativeDistributions.description

            val desktopFile = project.layout.buildDirectory.file("app-image/$buildDir/AppDir/usr/share/applications/$packageName.desktop")
            val desktopLink = project.layout.buildDirectory.file("app-image/$buildDir/AppDir/$packageName.desktop")

            val appRunTarget = project.layout.buildDirectory.file("app-image/$buildDir/AppDir/usr/bin/$packageName")
            val appRunLink = project.layout.buildDirectory.file("app-image/$buildDir/AppDir/AppRun")

            val mimeInfo = project.layout.buildDirectory.file("app-image/$buildDir/AppDir/usr/share/mime/packages/$packageName.xml")

            outputs.files(desktopFile, appRunLink, desktopLink, mimeInfo)

            doLast {
                desktopFile.get().asFile.writeText(
                    """
                    #!/usr/bin/env xdg-open
                    [Desktop Entry]
                    Name=$packageName
                    Comment=$description
                    Exec=AppRun
                    Icon=$packageName
                    Terminal=false
                    Type=Application
                    Categories=$menuGroup
                    MimeType=application/x-benzol
                """.trimIndent()
                )

                mimeInfo.get().asFile.writeText(
                    """
                    <?xml version="1.0" ?>
                    <mime-info xmlns="http://www.freedesktop.org/standards/shared-mime-info">
                      <mime-type type="application/x-benzol">
                        <comment>Benzol Betriebsanweisung</comment>
                        <glob pattern="*.benzol"></glob>
                      </mime-type>
                    </mime-info>    
                """.trimIndent()
                )

                desktopLink.get().asFile.toPath().createSymbolicLinkPointingTo(desktopFile.get().asFile.toPath())
                appRunLink.get().asFile.toPath().createSymbolicLinkPointingTo(appRunTarget.get().asFile.toPath())
            }
        }

        project.tasks.register("packageActual${name}AppImage", Exec::class.java) {
            group = "app-image"
            dependsOn(createAppDirTask, downloadAppImageTool)
            val nativeDistribution = compose.desktop.application.nativeDistributions
            val packageName = nativeDistribution.packageName
            val packageVersion = nativeDistribution.packageVersion

            onlyIf {
                org.gradle.internal.os.OperatingSystem.current().isLinux
            }

            val output = project.layout.buildDirectory.file("app-image/$buildDir/${packageName}-${packageVersion}.AppImage")

            outputs.file(output)

            environment("VERSION", packageVersion as Any)
            workingDir(project.layout.buildDirectory.dir("app-image/$buildDir/"))
            standardOutput = System.out
            executable = project.layout.buildDirectory.file("appimagetool/appimagetool-x86_64.AppImage").get().asFile.path
            setArgs(listOf("--comp", "zstd", "-n", "AppDir", output.get().asFile.path))
        }
    }
}