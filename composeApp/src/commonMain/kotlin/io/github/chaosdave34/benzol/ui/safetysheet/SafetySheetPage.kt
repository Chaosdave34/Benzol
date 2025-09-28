package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.CustomTextField
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.chaosdave34.benzol.ui.Section
import io.github.chaosdave34.benzol.ui.safetysheet.search.SubstanceSearch
import org.jetbrains.compose.resources.stringResource

context(viewModel: SafetySheetViewModel)
@Composable
fun SafetySheetPage() {
    val inputState by viewModel.inputState.collectAsState()

    var editSubstanceDialogVisible by rememberSaveable { mutableStateOf(false) }
    var selectedSubstance by remember { mutableIntStateOf(0) }

    EditSubstanceDialog(
        visible = editSubstanceDialogVisible,
        substance = inputState.substances.getOrNull(selectedSubstance),
        onDismissRequest = { editSubstanceDialogVisible = false },
        onEdit = {
            viewModel.updateSubstance(selectedSubstance, it)
            editSubstanceDialogVisible = false
        }
    )

    AppPageBox(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.TopCenter,
    ) { scrollState ->
        Column(
            Modifier
                .widthIn(min = 600.dp, max = 1300.dp) // Todo use adaptive
                .fillMaxWidth(0.8f)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Section {
                CustomTextField(
                    value = inputState.filename,
                    onValueChange = viewModel::setFilename,
                    label = stringResource(Res.string.filename)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp
                )
                CustomTextField(
                    value = inputState.documentTitle,
                    onValueChange = viewModel::setDocumentTitle,
                    label = stringResource(Res.string.document_title)
                )
                CustomTextField(
                    value = inputState.organisation,
                    onValueChange = viewModel::setOrganisation,
                    label = stringResource(Res.string.organisation)
                )
                CustomTextField(
                    value = inputState.course,
                    onValueChange = viewModel::setCourse,
                    label = stringResource(Res.string.course)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.name,
                        onValueChange = viewModel::setName,
                        label = stringResource(Res.string.name)
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.place,
                        onValueChange = viewModel::setPlace,
                        label = stringResource(Res.string.place)
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.assistant,
                        onValueChange = viewModel::setAssistant,
                        label = stringResource(Res.string.assistant)
                    )
                }
                CustomTextField(
                    value = inputState.preparation,
                    onValueChange = viewModel::setPreparation,
                    label = stringResource(Res.string.preparation)
                )
            }

            Section {
                SubstanceSearch(
                    onSearch = viewModel::addSubstance,
                    currentCasNumbers = inputState.substances.map { it.casNumber }
                )
            }

            Section {
                SubstanceList(
                    substances = inputState.substances,
                    onSubstanceClick = {
                        editSubstanceDialogVisible = true
                        selectedSubstance = it
                    },
                    onRemove = viewModel::removeSubstance
                )
                FilledIconButton(
                    onClick = { viewModel.addSubstance(Substance()) }
                ) {
                    Icon(Icons.Filled.Add, stringResource(Res.string.add))
                }
            }

            Section {
                ListInput(
                    title = stringResource(Res.string.human_and_environment_danger),
                    list = inputState.humanAndEnvironmentDanger,
                    onRemove = viewModel::removeHumanAndEnvironmentDanger,
                    onValueChange = viewModel::updateHumanAndEnvironmentDanger,
                    onAdd = { viewModel.addHumanAndEnvironmentDanger("") }
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.rules_of_conduct),
                    list = inputState.rulesOfConduct,
                    onRemove = viewModel::removeRuleOfConduct,
                    onValueChange = viewModel::updateRuleOfConduct,
                    onAdd = { viewModel.addRuleOfConduct("") }
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.in_case_of_danger),
                    list = inputState.inCaseOfDanger,
                    onRemove = viewModel::removeInCaseOfDanger,
                    onValueChange = viewModel::updateInCaseOfDanger,
                    onAdd = { viewModel.addInCaseOfDanger("") }
                )
            }
            Section {
                ListInput(
                    title = stringResource(Res.string.disposal),
                    list = inputState.disposal,
                    onRemove = viewModel::removeDisposal,
                    onValueChange = viewModel::updateDisposal,
                    onAdd = { viewModel.addDisposal("") }
                )
            }
        }
    }
}




