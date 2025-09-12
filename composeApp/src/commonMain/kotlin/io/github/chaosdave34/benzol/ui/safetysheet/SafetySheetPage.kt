package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.ui.*
import io.github.chaosdave34.benzol.ui.safetysheet.search.SubstanceSearch
import org.jetbrains.compose.resources.stringResource

@Composable
fun SafetySheetPage(
    viewModel: SafetySheetViewModel,
) {
    val inputState by viewModel.inputState.collectAsState()

    val substances by viewModel.substances.collectAsState()
    val humanAndEnvironmentDanger by viewModel.humanAndEnvironmentDanger.collectAsState()
    val rulesOfConduct by viewModel.rulesOfConduct.collectAsState()
    val inCaseOfDanger by viewModel.inCaseOfDanger.collectAsState()
    val disposal by viewModel.disposal.collectAsState()

    var editSubstanceDialogVisible by rememberSaveable { mutableStateOf(false) }
    var selectedSubstance by remember { mutableIntStateOf(0) }

    EditSubstanceDialog(
        visible = editSubstanceDialogVisible,
        substance = substances.getOrNull(selectedSubstance),
        onDissmissRequest = { editSubstanceDialogVisible = false },
        onEdit = {
            substances[selectedSubstance] = it
            editSubstanceDialogVisible = false
        }
    )

    AppPageBox(
        Modifier.fillMaxWidth(),
        title = stringResource(Res.string.sheet),
        contentAlignment = Alignment.TopCenter
    ) { scrollState ->
        Column(
            Modifier
                .widthIn(min = 600.dp, max = 1200.dp)
                .fillMaxWidth(0.8f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Section {
                CustomTextField(
                    value = inputState.fileName,
                    onValueChange = viewModel::setFileName,
                    label = Res.string.file_name
                )
                HorizontalDivider(thickness = 2.dp)
                CustomTextField(
                    value = inputState.documentTitle,
                    onValueChange = viewModel::setDocumentTitle,
                    label = Res.string.document_title
                )
                CustomTextField(
                    value = inputState.organisation,
                    onValueChange = viewModel::setOrganisation,
                    label = Res.string.organisation
                )
                CustomTextField(
                    value = inputState.course,
                    onValueChange = viewModel::setCourse,
                    label = Res.string.course
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.name,
                        onValueChange = viewModel::setName,
                        label = Res.string.name
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.place,
                        onValueChange = viewModel::setPlace,
                        label = Res.string.place
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.assistant,
                        onValueChange = viewModel::setAssistant,
                        label = Res.string.assistant
                    )
                }
                CustomTextField(
                    value = inputState.preparation,
                    onValueChange = viewModel::setPreparation,
                    label = Res.string.preparation
                )
            }

            Section {
                SubstanceSearch(
                    onSearch = {
                        substances.add(it)
                    }
                )
            }

            Section {
                SubstanceList(
                    substances = substances,
                    onSubstanceClick = {
                        editSubstanceDialogVisible = true
                        selectedSubstance = it
                    }
                )
                AddListElementButton(
                    list = substances,
                    element = Substance()
                )
            }

            Section {
                ListInput(
                    title = stringResource(Res.string.human_and_environment_danger),
                    list = humanAndEnvironmentDanger
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.rules_of_conduct),
                    list = rulesOfConduct
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.in_case_of_danger),
                    list = inCaseOfDanger
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.disposal),
                    list = disposal
                )
            }
        }
    }
}




