package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.data.SafetySheetInputState
import io.github.chaosdave34.benzol.ui.AddListElementButton
import io.github.chaosdave34.benzol.ui.AppPageBox
import io.github.chaosdave34.benzol.ui.CustomTextField
import io.github.chaosdave34.benzol.ui.Section
import io.github.chaosdave34.benzol.ui.safetysheet.search.SubstanceSearch
import org.jetbrains.compose.resources.stringResource

@Composable
fun SafetySheetPage(
    snackbarHostState: SnackbarHostState,
    inputState: SafetySheetInputState,
    substances: SnapshotStateList<Substance>,
    humanAndEnvironmentDanger: SnapshotStateList<String>,
    rulesOfConduct: SnapshotStateList<String>,
    inCaseOfDanger: SnapshotStateList<String>,
    disposal: SnapshotStateList<String>,
    onFilenameChange: (String) -> Unit,
    onDocumentTitleChange: (String) -> Unit,
    onOrganisationChange: (String) -> Unit,
    onCourseChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onPlaceChange: (String) -> Unit,
    onAssistantChange: (String) -> Unit,
    onPreparationChange: (String) -> Unit,
) {
    var editSubstanceDialogVisible by rememberSaveable { mutableStateOf(false) }
    var selectedSubstance by remember { mutableIntStateOf(0) }

    EditSubstanceDialog(
        visible = editSubstanceDialogVisible,
        substance = substances.getOrNull(selectedSubstance),
        onDismissRequest = { editSubstanceDialogVisible = false },
        onEdit = {
            substances[selectedSubstance] = it
            editSubstanceDialogVisible = false
        }
    )

    AppPageBox(
        Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
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
                    onValueChange = onFilenameChange,
                    label = stringResource(Res.string.filename)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    thickness = 2.dp
                )
                CustomTextField(
                    value = inputState.documentTitle,
                    onValueChange = onDocumentTitleChange,
                    label = stringResource(Res.string.document_title)
                )
                CustomTextField(
                    value = inputState.organisation,
                    onValueChange = onOrganisationChange,
                    label = stringResource(Res.string.organisation)
                )
                CustomTextField(
                    value = inputState.course,
                    onValueChange = onCourseChange,
                    label = stringResource(Res.string.course)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.name,
                        onValueChange = onNameChange,
                        label = stringResource(Res.string.name)
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.place,
                        onValueChange = onPlaceChange,
                        label = stringResource(Res.string.place)
                    )
                    CustomTextField(
                        modifier = Modifier.weight(0.33f),
                        value = inputState.assistant,
                        onValueChange = onAssistantChange,
                        label = stringResource(Res.string.assistant)
                    )
                }
                CustomTextField(
                    value = inputState.preparation,
                    onValueChange = onPreparationChange,
                    label = stringResource(Res.string.preparation)
                )
            }

            Section {
                SubstanceSearch(
                    snackbarHostState = snackbarHostState,
                    onSearch = substances::add,
                    currentCasNumbers = substances.map { it.casNumber }
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




