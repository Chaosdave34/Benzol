package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.ui.components.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


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
) {
    val scrollState = rememberScrollState()

    var searchTypeIndex by remember { mutableStateOf(0) }

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

                            SubstanceList(
                                substanceList = substanceList
                            )
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
fun ListInput(
    title: StringResource,
    list: SnapshotStateList<String>
) {
    DefaultColumn {
        Text(stringResource(title))
        list.forEachIndexed { index, element ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
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
                    index = index,
                    padding = 10.dp
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