package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.CustomCard
import io.github.chaosdave34.benzol.ui.CustomTextField
import io.github.chaosdave34.benzol.ui.SafetySheetViewModel
import io.github.chaosdave34.benzol.ui.safetysheet.search.GestisSearch
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
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
    ) { scrollState ->
        Column(
            Modifier
                .widthIn(min = WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp, max = WindowSizeClass.WIDTH_DP_EXTRA_LARGE_LOWER_BOUND.dp)
                .fillMaxWidth(0.8f)
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CustomCard {
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

            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.search_substances))
                },
                supportingContent = {
                    Text(stringResource(Res.string.gestis_hint))
                }
            ) {
//                SubstanceSearch(
//                    onSearch = viewModel::addSubstance,
//                    currentCasNumbers = inputState.substances.map { it.casNumber }
//                )
                GestisSearch(
                    onResult = viewModel::addSubstance,
                    currentCasNumbers = inputState.substances.map { it.casNumber }
                )
            }

            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.used_substances).replace("\n", " "))
                },
                supportingContent = {
                    Text(stringResource(Res.string.edit_substance))
                }
            ) {
                SubstanceList(
                    substances = inputState.substances,
                    onSubstanceClick = {
                        editSubstanceDialogVisible = true
                        selectedSubstance = it
                    },
                    onRemove = viewModel::removeSubstance,
                    onDrag = viewModel::onSubstanceDrag
                )
                FilledIconButton(
                    onClick = { viewModel.addSubstance(Substance()) }
                ) {
                    Icon(Icons.Filled.Add, stringResource(Res.string.add))
                }
            }

            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.human_and_environment_danger))
                }
            ) {
                ListInput(
                    list = inputState.humanAndEnvironmentDanger,
                    onRemove = viewModel::removeHumanAndEnvironmentDanger,
                    onValueChange = viewModel::updateHumanAndEnvironmentDanger,
                    onAdd = { viewModel.addHumanAndEnvironmentDanger("") },
                    onDrag = viewModel::onHumanAndEnvironmentDangerDrag
                )
            }
            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.rules_of_conduct))
                }
            ) {
                ListInput(
                    list = inputState.rulesOfConduct,
                    onRemove = viewModel::removeRuleOfConduct,
                    onValueChange = viewModel::updateRuleOfConduct,
                    onAdd = { viewModel.addRuleOfConduct("") },
                    onDrag = viewModel::onRuleOfConductDrag
                )
            }
            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.in_case_of_danger))
                }
            ) {
                ListInput(
                    list = inputState.inCaseOfDanger,
                    onRemove = viewModel::removeInCaseOfDanger,
                    onValueChange = viewModel::updateInCaseOfDanger,
                    onAdd = { viewModel.addInCaseOfDanger("") },
                    onDrag = viewModel::onInCaseOfDangerDrag
                )
            }
            CustomCard(
                headlineContent = {
                    Text(stringResource(Res.string.disposal))
                }
            ) {
                ListInput(
                    list = inputState.disposal,
                    onRemove = viewModel::removeDisposal,
                    onValueChange = viewModel::updateDisposal,
                    onAdd = { viewModel.addDisposal("") },
                    onDrag = viewModel::onDisposalDrag
                )
            }
        }
    }
}




