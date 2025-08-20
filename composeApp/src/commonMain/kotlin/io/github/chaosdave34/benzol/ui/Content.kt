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


@Composable
fun Content(
    viewModel: SafetySheetViewModel,
) {
    val inputState by viewModel.inputState.collectAsState()

    val substances by viewModel.substances.collectAsState()
    val humanAndEnvironmentDanger by viewModel.humanAndEnvironmentDanger.collectAsState()
    val rulesOfConduct by viewModel.rulesOfConduct.collectAsState()
    val inCaseOfDanger by viewModel.inCaseOfDanger.collectAsState()
    val disposal by viewModel.disposal.collectAsState()

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
                            value = inputState.fileName,
                            onValueChange = viewModel::setFileName,
                            label = Res.string.file_name
                        )
                        HorizontalDivider()
                        Input(
                            value = inputState.documentTitle,
                            onValueChange = viewModel::setDocumentTitle,
                            label = Res.string.document_title
                        )
                        Input(
                            value = inputState.organisation,
                            onValueChange = viewModel::setOrganisation,
                            label = Res.string.organisation
                        )
                        Input(
                            value = inputState.course,
                            onValueChange = viewModel::setCourse,
                            label = Res.string.course
                        )
                        Input(
                            value = inputState.name,
                            onValueChange = viewModel::setName,
                            label = Res.string.name
                        )
                        Input(
                            value = inputState.place,
                            onValueChange = viewModel::setPlace,
                            label = Res.string.place
                        )
                        Input(
                            value = inputState.assistant,
                            onValueChange = viewModel::setAssistant,
                            label = Res.string.assistant
                        )
                        Input(
                            value = inputState.preparation,
                            onValueChange = viewModel::setPreparation,
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
                                onResult = { substances.add(it) }
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
                                substances = substances
                            )
                        }
                        AddListElementButton(
                            list = substances,
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
                            inputState = inputState,
                            substances = substances,
                            humanAndEnvironmentDanger = humanAndEnvironmentDanger,
                            rulesOfConduct = rulesOfConduct,
                            inCaseOfDanger = inCaseOfDanger,
                            disposal = disposal,
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
                    onClick = viewModel::openFileSaver
                ) {
                    Text(stringResource(Res.string.save_file))
                }
                Button(
                    onClick = viewModel::openPdfExport
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