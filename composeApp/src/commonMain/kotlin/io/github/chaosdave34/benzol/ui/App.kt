package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.FileOpener
import io.github.chaosdave34.benzol.FileSaver
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.PdfExport
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.files.CaBr2File
import io.github.chaosdave34.benzol.files.HtmlFile
import io.github.chaosdave34.benzol.getSettings
import io.github.chaosdave34.benzol.search.Source
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        GHSPictogram.Companion.setBase64()
    }

    val settings = getSettings()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val documentTitleDefault = stringResource(Res.string.document_title_default)
    val organisationDefault = stringResource(Res.string.organisation_default)
    val courseDefault = stringResource(Res.string.course_default)
    val inCaseOfDangerDefaults = stringArrayResource(Res.array.in_case_of_danger_defaults)

    val fileName = remember { mutableStateOf("") }

    val documentTitle = remember { mutableStateOf(documentTitleDefault) }
    val organisation = remember { mutableStateOf(organisationDefault) }
    val course = remember { mutableStateOf(courseDefault) }
    val name = remember { mutableStateOf("") }
    val place = remember { mutableStateOf("") }
    val assistant = remember { mutableStateOf("") }
    val preparation = remember { mutableStateOf("") }

    val humanAndEnvironmentDanger = remember { mutableStateListOf<String>() }
    val rulesOfConduct = remember { mutableStateListOf<String>() }
    val inCaseOfDanger = remember { inCaseOfDangerDefaults.toMutableStateList() }
    val disposal = remember { mutableStateListOf<String>() }

    val substanceList = remember { mutableStateListOf<Substance>() }

    var searchTypeIndex by remember { mutableStateOf(0) }

    var dialogOpen: Int? by remember { mutableStateOf(null) }

    var sidebarDropdownExpanded by remember { mutableStateOf(false) }

    var fileOpenerVisible by remember { mutableStateOf(false) }
    var fileSaverVisible by remember { mutableStateOf(false) }

    var pdfExportVisible by remember { mutableStateOf(false) }

    var openLink: String? by remember { mutableStateOf(null) }

    var darkTheme = remember { mutableStateOf(settings.getBoolean("dark_theme", false)) }
    var settingsOpen = remember { mutableStateOf(false) }

    openLink?.let {
        LocalUriHandler.current.openUri(it)
        openLink = null
    }

    MaterialTheme(
        colorScheme = if (darkTheme.value) darkColorScheme() else lightColorScheme()
    ) {
        if (settingsOpen.value) {
            Settings(settingsOpen, darkTheme)
        }

        Disclaimer()

        if (fileOpenerVisible) {
            FileOpener(
                coroutineScope,
                result = { json, file ->
                    if (json != null) {
                        val caBr2File = CaBr2File.fromJson(json)
                        if (caBr2File != null) {
                            val header = caBr2File.header
                            documentTitle.value = header.documentTitle
                            organisation.value = header.organisation
                            course.value = header.labCourse
                            name.value = header.name
                            place.value = header.place
                            assistant.value = header.assistant
                            preparation.value = header.preparation

                            humanAndEnvironmentDanger.clear()
                            humanAndEnvironmentDanger.addAll(caBr2File.humanAndEnvironmentDanger)

                            rulesOfConduct.clear()
                            rulesOfConduct.addAll(caBr2File.rulesOfConduct)

                            inCaseOfDanger.clear()
                            inCaseOfDanger.addAll(caBr2File.inCaseOfDanger)

                            disposal.clear()
                            disposal.addAll(caBr2File.disposal)

                            substanceList.clear()
                            substanceList.addAll(caBr2File.substanceData.map { it.import() })

                            fileName.value = file.replace("\\.[^.]*$".toRegex(), "")
                        }
                    }
                }
            ) {
                fileOpenerVisible = false
            }
        }

        if (fileSaverVisible) {
            FileSaver(
                coroutineScope,
                fileName = fileName.value,
                output = {
                    val header = CaBr2File.CaBr2Data.Header(
                        documentTitle.value.trim(),
                        organisation.value.trim(),
                        course.value.trim(),
                        name.value.trim(),
                        place.value.trim(),
                        assistant.value.trim(),
                        preparation.value.trim()
                    )

                    val content = CaBr2File.CaBr2Data(
                        header,
                        substanceList.map { CaBr2File.CaBr2Data.SubstanceData.export(it) },
                        humanAndEnvironmentDanger.map { it.trim() },
                        rulesOfConduct.map { it.trim() },
                        inCaseOfDanger.map { it.trim() },
                        disposal.map { it.trim() }
                    )

                    CaBr2File.toJson(content)
                }
            ) {
                fileSaverVisible = false
            }
        }

        if (pdfExportVisible) {
            Dialog(
                onDismissRequest = {}
            ) {
                CircularProgressIndicator()
            }

            PdfExport(
                coroutineScope,
                fileName = fileName.value,
                output = {
                    HtmlFile(
                        documentTitle.value.trim(),
                        organisation.value.trim(),
                        course.value.trim(),
                        name.value.trim(),
                        place.value.trim(),
                        assistant.value.trim(),
                        preparation.value.trim(),
                        substanceList,
                        humanAndEnvironmentDanger.map { it.trim(); it.replace("\n", "") },
                        rulesOfConduct.map { it.trim(); it.replace("\n", "") },
                        inCaseOfDanger.map { it.trim(); it.replace("\n", "") },
                        disposal.map { it.trim(); it.replace("\n", "") }
                    )
                }
            ) {
                pdfExportVisible = false
            }
        }

        Row(
            Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focusManager.clearFocus()
                }
            }
        ) {
            Surface(
                tonalElevation = 8.dp
            ) {
                Column(
                    Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box {
                        Button(
                            onClick = { sidebarDropdownExpanded = true }
                        ) {
                            Icon(Icons.Rounded.Menu, stringResource(Res.string.open_settings))
                        }

                        DropdownMenu(
                            expanded = sidebarDropdownExpanded,
                            onDismissRequest = { sidebarDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.new_file)) },
                                onClick = {
                                    sidebarDropdownExpanded = false
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
                            DropdownMenuItem(
                                text = { Text(stringResource(Res.string.open_file)) },
                                onClick = {
                                    sidebarDropdownExpanded = false
                                    fileOpenerVisible = true
                                }
                            )
                            DropdownMenuItem(
                                {
                                    Text(stringResource(Res.string.save_file))
                                },
                                onClick = {
                                    sidebarDropdownExpanded = false
                                    fileSaverVisible = true
                                }
                            )
                            DropdownMenuItem(
                                { Text(stringResource(Res.string.export_file)) },
                                onClick = {
                                    sidebarDropdownExpanded = false
                                    pdfExportVisible = true
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                { Text(stringResource(Res.string.settings)) },
                                onClick = {
                                    sidebarDropdownExpanded = false
                                    settingsOpen.value = true
                                }
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            openLink = "https://github.com/Chaosdave34/"
                        }
                    ) {
                        Image(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            painter = painterResource(Res.drawable.github),
                            contentDescription = stringResource(Res.string.github)
                        )
                    }

                    Text(text = "1.0.0")
                }
            }

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
                                                        openLink = substance.source.second
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
                                fileSaverVisible = true
                            }
                        ) {
                            Text(stringResource(Res.string.save_file))
                        }
                        Button(
                            onClick = {
                                pdfExportVisible = true
                            }
                        ) {
                            Text(stringResource(Res.string.export_file))
                        }
                    }
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