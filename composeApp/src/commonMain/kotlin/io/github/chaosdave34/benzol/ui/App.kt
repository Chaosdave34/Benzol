package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.FileDialogs
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.files.InputData
import io.github.chaosdave34.benzol.getSettings
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val settings = getSettings()

    LaunchedEffect(Unit) {
        GHSPictogram.Companion.setBase64()
    }

    val darkTheme = remember { mutableStateOf(settings.getBoolean("dark_theme", false)) }

    val fileChooserVisible = remember { mutableStateOf(false) }
    val fileSaverVisible = remember { mutableStateOf(false) }
    val pdfExportVisible = remember { mutableStateOf(false) }

    val settingsVisible = remember { mutableStateOf(false) }

    // Links
    var openLink: String? by remember { mutableStateOf(null) }
    openLink?.let {
        LocalUriHandler.current.openUri(it)
        openLink = null
    }

    // default values
    val documentTitleDefault = stringResource(Res.string.document_title_default)
    val organisationDefault = stringResource(Res.string.organisation_default)
    val courseDefault = stringResource(Res.string.course_default)
    val inCaseOfDangerDefaults = stringArrayResource(Res.array.in_case_of_danger_defaults)
    val rulesOfConductDefaults = stringArrayResource(Res.array.rules_of_conduct_defaults)

    // input
    val fileName = remember { mutableStateOf("") }

    val documentTitle = remember { mutableStateOf(documentTitleDefault) }
    val organisation = remember { mutableStateOf(organisationDefault) }
    val course = remember { mutableStateOf(courseDefault) }
    val name = remember { mutableStateOf("") }
    val place = remember { mutableStateOf("") }
    val assistant = remember { mutableStateOf("") }
    val preparation = remember { mutableStateOf("") }

    val humanAndEnvironmentDanger = remember { mutableStateListOf<String>() }
    val rulesOfConduct = remember { rulesOfConductDefaults.toMutableStateList() }
    val inCaseOfDanger = remember { inCaseOfDangerDefaults.toMutableStateList() }
    val disposal = remember { mutableStateListOf<String>() }

    val substanceList = remember { mutableStateListOf<Substance>() }

    FileDialogs(
        coroutineScope = coroutineScope,
        fileChooserVisible = fileChooserVisible,
        fileSaverVisible = fileSaverVisible,
        pdfExportVisible = pdfExportVisible,
        import = { inputData ->
            fileName.value = inputData.fileName
            documentTitle.value = inputData.documentTitle
            organisation.value = inputData.organisation
            course.value = inputData.course
            name.value = inputData.name
            place.value = inputData.place
            assistant.value = inputData.assistant
            preparation.value = inputData.preparation

            substanceList.clear()
            substanceList.addAll(inputData.substanceList)

            humanAndEnvironmentDanger.clear()
            humanAndEnvironmentDanger.addAll(inputData.humanAndEnvironmentDanger)

            rulesOfConduct.clear()
            rulesOfConduct.addAll(inputData.rulesOfConduct)

            inCaseOfDanger.clear()
            inCaseOfDanger.addAll(inputData.inCaseOfDanger)

            disposal.clear()
            disposal.addAll(inputData.disposal)
        },
        export = {
            InputData(
                fileName = fileName.value,
                documentTitle = documentTitle.value,
                organisation = organisation.value,
                course = course.value,
                name = name.value,
                place = place.value,
                assistant = assistant.value,
                preparation = preparation.value,
                substanceList = substanceList,
                humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                rulesOfConduct = rulesOfConduct,
                inCaseOfDanger = inCaseOfDanger,
                disposal = disposal
            )
        }
    )

    MaterialTheme(
        colorScheme = if (darkTheme.value) darkColorScheme() else lightColorScheme()
    ) {
        Settings(
            open = settingsVisible,
            darkTheme = darkTheme
        )
        Disclaimer()

        Row(
            Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
        ) {
            Sidebar(
                openFileChooser = { fileChooserVisible.value = true },
                openFileSaver = { fileSaverVisible.value = true },
                openPdfExport = { pdfExportVisible.value = true },
                openSettings = { settingsVisible.value = true },
                openLink = { openLink = it },
                resetInput = {
                    fileName.value = ""

                    documentTitle.value = documentTitleDefault
                    organisation.value = organisationDefault
                    course.value = courseDefault
                    name.value = ""
                    place.value = ""
                    assistant.value = ""
                    preparation.value = ""

                    humanAndEnvironmentDanger.clear()
                    rulesOfConduct.clear()
                    inCaseOfDanger.clear()
                    inCaseOfDanger.addAll(inCaseOfDangerDefaults)
                    disposal.clear()

                    substanceList.clear()
                }
            )

            Content(
                fileName = fileName,
                documentTitle = documentTitle,
                organisation = organisation,
                course = course,
                name = name,
                place = place,
                assistant = assistant,
                preparation = preparation,
                substanceList = substanceList,
                humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                rulesOfConduct = rulesOfConduct,
                inCaseOfDanger = inCaseOfDanger,
                disposal = disposal,
                openFileSaver = { fileSaverVisible.value = true },
                openPdfExport = { pdfExportVisible.value = true },
                openLink = { openLink = it }
            )
        }
    }
}