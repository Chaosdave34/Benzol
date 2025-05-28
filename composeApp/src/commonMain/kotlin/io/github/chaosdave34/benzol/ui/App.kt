package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import com.russhwolf.settings.set
import io.github.chaosdave34.benzol.*
import io.github.chaosdave34.benzol.files.InputData
import io.ktor.util.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }

    val settings = getSettings()

    val darkTheme = remember { mutableStateOf(settings.getBoolean("dark_theme", false)) }

    val locale = if (PlatformUtils.IS_BROWSER) Locale.current.language else settings.getString("language", SupportedLanguage.GERMAN.locale)
    var appLanguage by remember { mutableStateOf(SupportedLanguage.fromLocale(locale) ?: SupportedLanguage.GERMAN) }

    val fileChooserVisible = remember { mutableStateOf(false) }
    val fileSaverVisible = remember { mutableStateOf(false) }
    val pdfExportVisible = remember { mutableStateOf(false) }

    val settingsVisible = remember { mutableStateOf(false) }

    // input
    val fileName = remember { mutableStateOf("") }

    val documentTitle = remember { mutableStateOf("") }
    val organisation = remember { mutableStateOf("") }
    val course = remember { mutableStateOf("") }
    val name = remember { mutableStateOf("") }
    val place = remember { mutableStateOf("") }
    val assistant = remember { mutableStateOf("") }
    val preparation = remember { mutableStateOf("") }

    val humanAndEnvironmentDanger = remember { mutableStateListOf<String>() }
    val rulesOfConduct = remember { mutableStateListOf<String>() }
    val inCaseOfDanger = remember { mutableStateListOf<String>() }
    val disposal = remember { mutableStateListOf<String>() }

    val substanceList = remember { mutableStateListOf<Substance>() }

    suspend fun defaultValues() {
        documentTitle.value = getString(Res.string.document_title_default)
        organisation.value = getString(Res.string.organisation_default)
        course.value = getString(Res.string.course_default)
        inCaseOfDanger.addAll(getStringArray(Res.array.in_case_of_danger_defaults))
        rulesOfConduct.addAll(getStringArray(Res.array.rules_of_conduct_defaults))
    }

    if (Locale.current.language != appLanguage.locale) {
        setLanguage(appLanguage)
    }

    val localLanguage = compositionLocalOf { appLanguage }
    CompositionLocalProvider(localLanguage provides appLanguage) {

        LaunchedEffect(Unit) {
            GHSPictogram.Companion.setBase64()
            defaultValues()
        }

        FileDialogs(
            coroutineScope = coroutineScope,
            fileChooserVisible = fileChooserVisible,
            fileSaverVisible = fileSaverVisible,
            pdfExportVisible = pdfExportVisible,
            snackbarHostState = snackbarHostState,
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
                language = appLanguage,
                setLanguage = { newLanguage ->
                    appLanguage = newLanguage
                    setLanguage(newLanguage)
                    settings["language"] = newLanguage.locale
                },
                darkTheme = darkTheme
            )
            Disclaimer()

            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .padding(bottom = 60.dp)
                            .fillMaxWidth(0.7f),
                    )
                }
            ) {
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
                        resetInput = {
                            fileName.value = ""

                            documentTitle.value = ""
                            organisation.value = ""
                            course.value = ""
                            name.value = ""
                            place.value = ""
                            assistant.value = ""
                            preparation.value = ""

                            humanAndEnvironmentDanger.clear()
                            rulesOfConduct.clear()
                            inCaseOfDanger.clear()
                            inCaseOfDanger.clear()
                            disposal.clear()

                            substanceList.clear()

                            coroutineScope.launch {
                                defaultValues()
                            }
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
                        openPdfExport = { pdfExportVisible.value = true }
                    )
                }
            }
        }
    }
}