package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.FileDialogs
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.files.InputData
import io.github.chaosdave34.benzol.getSettings
import io.github.chaosdave34.benzol.search.Source
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val settings = getSettings()

    coroutineScope.launch {
        GHSPictogram.Companion.setBase64()
    }

    var darkTheme = remember { mutableStateOf(settings.getBoolean("dark_theme", false)) }

    var fileChooserVisible = remember { mutableStateOf(false) }
    var fileSaverVisible = remember { mutableStateOf(false) }
    var pdfExportVisible = remember { mutableStateOf(false) }

    var settingsVisible = remember { mutableStateOf(false) }

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

@Composable
fun Sidebar(
    openFileChooser: () -> Unit,
    openFileSaver: () -> Unit,
    openPdfExport: () -> Unit,
    openSettings: () -> Unit,
    openLink: (String) -> Unit,
    resetInput: () -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 8.dp
    ) {
        Column(
            Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Button(
                    onClick = { dropdownExpanded = true }
                ) {
                    Icon(Icons.Rounded.Menu, stringResource(Res.string.open_settings))
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.new_file)) },
                        onClick = {
                            dropdownExpanded = false
                            resetInput()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.open_file)) },
                        onClick = {
                            dropdownExpanded = false
                            openFileChooser()
                        }
                    )
                    DropdownMenuItem(
                        {
                            Text(stringResource(Res.string.save_file))
                        },
                        onClick = {
                            dropdownExpanded = false
                            openFileSaver()
                        }
                    )
                    DropdownMenuItem(
                        { Text(stringResource(Res.string.export_file)) },
                        onClick = {
                            dropdownExpanded = false
                            openPdfExport()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        { Text(stringResource(Res.string.settings)) },
                        onClick = {
                            dropdownExpanded = false
                            openSettings()
                        }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    openLink("https://github.com/Chaosdave34/Benzol")
                }
            ) {
                Image(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    painter = painterResource(Res.drawable.github),
                    contentDescription = stringResource(Res.string.github)
                )
            }
            Text("1.1.1")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(
    fileName: MutableState<String>,
    documentTitle: MutableState<String>,
    organisation: MutableState<String>,
    course: MutableState<String>,
    name: MutableState<String>,
    place: MutableState<String>,
    assistant: MutableState<String>,
    preparation: MutableState<String>,
    substanceList: SnapshotStateList<Substance>,
    humanAndEnvironmentDanger: SnapshotStateList<String>,
    rulesOfConduct: SnapshotStateList<String>,
    inCaseOfDanger: SnapshotStateList<String>,
    disposal: SnapshotStateList<String>,
    openFileSaver: () -> Unit,
    openPdfExport: () -> Unit,
    openLink: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    var searchTypeIndex by remember { mutableStateOf(0) }
    var dialogOpen: Int? by remember { mutableStateOf(null) }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .verticalScroll(scrollState)
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DefaultColumn {
                        Input(
                            value = fileName,
                            label = Res.string.file_name
                        )
                        HorizontalDivider()
                        Input(
                            value = documentTitle,
                            label = Res.string.document_title
                        )
                        Input(
                            value = organisation,
                            label = Res.string.organisation
                        )
                        Input(
                            value = course,
                            label = Res.string.course
                        )
                        Input(
                            value = name,
                            label = Res.string.name
                        )
                        Input(
                            value = place,
                            label = Res.string.place
                        )
                        Input(
                            value = assistant,
                            label = Res.string.assistant
                        )
                        Input(
                            value = preparation,
                            label = Res.string.preparation
                        )
                    }

                    DefaultColumn {
                        TabRow(
                            selectedTabIndex = searchTypeIndex,
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Tab(
                                selected = searchTypeIndex == 0,
                                onClick = { searchTypeIndex = 0 },
                                text = { Text(stringResource(Res.string.gestis)) },
                            )

                            Tab(
                                selected = searchTypeIndex == 1,
                                enabled = false,
                                onClick = { searchTypeIndex = 1 },
                                text = { Text("") },
                            )
                        }

                        when (searchTypeIndex) {
                            0 -> GestisSearch(
                                onResult = { substanceList.add(it) }
                            )
                        }

                        HorizontalDivider(thickness = 2.dp)
                        Text(stringResource(Res.string.edit_substance))

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    modifier = Modifier.weight(0.25f),
                                    text = stringResource(Res.string.name)
                                )
                                Text(
                                    modifier = Modifier.weight(0.25f),
                                    text = stringResource(Res.string.molecular_formula)
                                )
                                Text(
                                    modifier = Modifier.weight(0.15f),
                                    text = stringResource(Res.string.cas_number)
                                )
                                Spacer(Modifier.weight(0.35f))
                            }

                            substanceList.forEachIndexed { index, substance ->
                                HorizontalDivider(thickness = 2.dp)
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable(onClick = { dialogOpen = index }),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        modifier = Modifier.weight(0.25f),
                                        text = substance.name
                                    )

                                    val formula = substance.formattedMolecularFormula
                                    if (formula.isNotBlank()) {
                                        substance.FormattedMolecularFormula(Modifier.weight(0.25f))
                                    } else {
                                        Text(
                                            modifier = Modifier.weight(0.25f),
                                            text = substance.molecularFormula
                                        )
                                    }
                                    Text(
                                        modifier = Modifier.weight(0.15f),
                                        text = substance.casNumber
                                    )
                                    Row(
                                        modifier = Modifier.weight(0.35f).padding(end = 10.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                                    ) {
                                        Button(
                                            onClick = {
                                                openLink(substance.source.second)
                                            },
                                            enabled = substance.source.first != Source.CUSTOM
                                        ) {
                                            Text(substance.source.first.displayName)
                                        }
                                        Button(
                                            onClick = { dialogOpen = index }
                                        ) {
                                            Icon(Icons.Rounded.Edit, stringResource(Res.string.open_settings))
                                        }
                                        MoveUpAndDown(
                                            list = substanceList,
                                            index = index
                                        )
                                        RemoveListElementButton(
                                            list = substanceList,
                                            index = index
                                        )
                                    }
                                }
                            }
                            val index = dialogOpen
                            if (index != null) {
                                EditSubstanceDialog(
                                    list = substanceList,
                                    index = index,
                                    onClose = { dialogOpen = null }
                                )
                            }
                        }
                        AddListElementButton(
                            list = substanceList,
                            element = Substance()
                        )
                    }

                    ListInput(
                        title = Res.string.human_and_environment_danger,
                        list = humanAndEnvironmentDanger
                    )
                    ListInput(
                        title = Res.string.rules_of_conduct,
                        list = rulesOfConduct
                    )
                    ListInput(
                        title = Res.string.in_case_of_danger,
                        list = inCaseOfDanger
                    )
                    ListInput(
                        title = Res.string.disposal,
                        list = disposal
                    )

                    DefaultColumn {
                        Preview(
                            documentTitle.value.trim(),
                            organisation.value.trim(),
                            course.value.trim(),
                            name.value.trim(),
                            place.value.trim(),
                            assistant.value.trim(),
                            preparation.value.trim(),
                            substanceList,
                            humanAndEnvironmentDanger,
                            rulesOfConduct,
                            inCaseOfDanger,
                            disposal
                        )
                    }
                }

                Scrollbar(scrollState)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
            ) {
                Button(
                    onClick = {
                        openFileSaver()
                    }
                ) {
                    Text(stringResource(Res.string.save_file))
                }
                Button(
                    onClick = {
                        openPdfExport()
                    }
                ) {
                    Text(stringResource(Res.string.export_file))
                }
            }
        }
    }
}

@Composable
fun DefaultColumn(content: @Composable (ColumnScope.() -> Unit)) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content
    )
}

@Composable
fun Input(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: MutableState<String>,
    onChange: (String) -> Unit = { value.value = it },
    label: StringResource,
    supportingText: @Composable (() -> Unit)? = null
) {
    TextField(
        modifier = modifier,
        value = value.value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        singleLine = true,
        supportingText = supportingText
    )
}

@Composable
fun ListInput(
    title: StringResource,
    list: SnapshotStateList<String>
) {
    DefaultColumn {
        Text(stringResource(title))
        list.forEachIndexed { index, element ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("•")
                TextField(
                    modifier = Modifier.weight(1f),
                    value = element,
                    onValueChange = { list[index] = it },
                )
                MoveUpAndDown(
                    list = list,
                    index = index
                )
                RemoveListElementButton(
                    list = list,
                    index = index
                )
            }
        }
        AddListElementButton(
            list = list,
            element = ""
        )
    }
}

@Composable
fun <T> MoveUpAndDown(
    list: SnapshotStateList<T>,
    index: Int
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Bottom
    ) {
        if (index != 0) {
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    if (index > 0) {
                        list[index] = list.set(index - 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.up)
            )
        }
        if (index != list.lastIndex) {
            Icon(
                modifier = Modifier.clip(CircleShape).clickable {
                    if (index < list.lastIndex) {
                        list[index] = list.set(index + 1, list[index])
                    }
                },
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.down)
            )
        }
    }
}

@Composable
fun Scrollbar(scrollState: ScrollState) {
    VerticalScrollbar(
        modifier = Modifier.fillMaxHeight(),
        adapter = rememberScrollbarAdapter(scrollState),
        style = defaultScrollbarStyle().copy(
            hoverColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unhoverColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}

@Composable
fun <T> AddListElementButton(
    list: SnapshotStateList<T>,
    element: T
) {
    Button(
        onClick = { list.add(element) },
    ) { Icon(Icons.Rounded.Add, stringResource(Res.string.add)) }
}

@Composable
fun RemoveListElementButton(
    list: SnapshotStateList<*>,
    index: Int
) {
    Button(
        onClick = { if (index >= 0 && index <= list.lastIndex) list.removeAt(index) },
    ) { Icon(Icons.Rounded.Delete, stringResource(Res.string.delete)) }
}

@Composable
fun GHSPictogram(modifier: Modifier = Modifier, pictogram: GHSPictogram, selected: SnapshotStateList<GHSPictogram>) {
    val isSelected = selected.contains(pictogram)
    Image(
        modifier = modifier.clickable(onClick = {
            if (isSelected) selected.remove(pictogram) else selected.add(pictogram)
        }),
        painter = painterResource(pictogram.drawableResource),
        contentDescription = pictogram.alt,
        colorFilter = if (!isSelected) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
    )
}