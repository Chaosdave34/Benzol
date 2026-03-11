package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.*
import io.github.chaosdave34.benzol.ui.*
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditSubstanceDialog(
    visible: Boolean,
    substance: Substance?,
    onDismissRequest: () -> Unit,
    onEdit: (Substance) -> Unit,
) {
    if (substance != null && visible) {
        var name by remember { mutableStateOf(substance.name) }
        var casNumber by remember { mutableStateOf(substance.casNumber) }
        var molecularFormula by remember { mutableStateOf(substance.molecularFormula) }
        var wgk by remember { mutableStateOf(substance.wgk) }
        var signalWord by remember { mutableStateOf(substance.signalWord) }

        var molarMass by remember { mutableStateOf(substance.molarMass) }
        var lethalDose by remember { mutableStateOf(substance.lethalDose) }
        var mak by remember { mutableStateOf(substance.mak) }
        var meltingPoint by remember { mutableStateOf(substance.meltingPoint) }
        var boilingPoint by remember { mutableStateOf(substance.boilingPoint) }
        var quantity by remember { mutableStateOf(substance.quantity.value) }
        var quantityUnit by remember { mutableStateOf(substance.quantity.unit) }

        val hPhrases = remember { mutableStateListOf<Pair<String, String>>().also { it.addAll(substance.hPhrases) } }
        val pPhrases = remember { mutableStateListOf<Pair<String, String>>().also { it.addAll(substance.pPhrases) } }
        val ghsPictograms = remember { mutableStateListOf<GHSPictogram>().also { it.addAll(substance.ghsPictograms) } }

        val numberRegex = "[0-9]+[,.]?[0-9]*".toRegex()
        val negativeNumberRegex = "-?[0-9]+[,.]?[0-9]*".toRegex()
        val casRegex = "([0-9]{2,8}-[0-9]{2}-[0-9])".toRegex()

        val onReset: () -> Unit = {
            name = substance.nameModifiable.original
            casNumber = substance.casNumberModifiable.original
            molecularFormula = substance.molecularFormulaModifiable.original
            wgk = substance.wgkModifiable.original
            signalWord = substance.signalWordModifiable.original

            molarMass = substance.molarMassModifiable.original
            lethalDose = substance.lethalDoseModifiable.original
            mak = substance.makModifiable.original
            meltingPoint = substance.meltingPointModifiable.original
            boilingPoint = substance.boilingPointModifiable.original
            quantity = Substance.Quantity().value
            quantityUnit = Substance.Quantity().unit

            hPhrases.clear()
            hPhrases.addAll(substance.hPhrasesModifiable.original)
            pPhrases.clear()
            pPhrases.addAll(substance.pPhrasesModifiable.original)
            ghsPictograms.clear()
            ghsPictograms.addAll(substance.ghsPictogramsModifiable.original)
        }

        val onSave: () -> Unit = {
            onEdit(
                substance.copyAsModified(
                    name.trim(),
                    casNumber.trim(),
                    molecularFormula.trim(),
                    wgk,
                    signalWord,
                    molarMass.trim(),
                    lethalDose.trim(),
                    mak.trim(),
                    meltingPoint.trim(),
                    boilingPoint.trim(),
                    Substance.Quantity(quantity.trim(), quantityUnit.trim()),
                    hPhrases.map { Pair(it.first.trim(), it.second.trim()) },
                    pPhrases.map { Pair(it.first.trim(), it.second.trim()) },
                    ghsPictograms
                )
            )
        }

        AdaptiveDialog(
            title = stringResource(Res.string.edit_substance_dialog),
            onDismissRequest = onDismissRequest,
            actions = {
                TextButton(
                    onClick = onReset
                ) {
                    Text(stringResource(Res.string.reset))
                }
                TextButton(
                    onClick = onSave
                ) {
                    Text(stringResource(Res.string.save))
                }
            }
        ) {
            val scrollState = rememberScrollState()

            Column(
                Modifier.verticalScroll(scrollState).padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.general))
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = name,
                            onValueChange = { name = it },
                            label = stringResource(Res.string.name)
                        )
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = casNumber,
                            onValueChange = { casNumber = it },
                            label = stringResource(Res.string.cas_number),
                            isError = !casRegex.matches(casNumber) && casNumber.isNotEmpty()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = molecularFormula,
                            onValueChange = { molecularFormula = it },
                            label = stringResource(Res.string.formatted_molecular_formula),
                            supportingText = {
                                if (molecularFormula.isBlank()) {
                                    Text(stringResource(Res.string.formatted_molecular_formula_hint))
                                } else {
                                    FormattedMolecularFormula(formula = molecularFormula)
                                }
                            }
                        )
                    }
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.properties))
                    }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = molarMass,
                            onValueChange = { molarMass = it },
                            label = stringResource(Res.string.molar_mass),
                            suffix = {
                                Text(stringResource(Res.string.molar_mass_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(molarMass) && molarMass.isNotEmpty()
                        )

                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = meltingPoint,
                            onValueChange = { meltingPoint = it },
                            label = stringResource(Res.string.melting_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !negativeNumberRegex.matches(meltingPoint) && meltingPoint.isNotEmpty()
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = boilingPoint,
                            onValueChange = { boilingPoint = it },
                            label = stringResource(Res.string.boiling_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !negativeNumberRegex.matches(boilingPoint) && boilingPoint.isNotEmpty()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = mak,
                            onValueChange = { mak = it },
                            label = stringResource(Res.string.mak),
                            suffix = {
                                Text(stringResource(Res.string.mak_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(mak) && mak.isNotEmpty()
                        )

                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = lethalDose,
                            onValueChange = { lethalDose = it },
                            label = stringResource(Res.string.lethal_dose),
                            suffix = {
                                Text(stringResource(Res.string.lethal_dose_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(lethalDose) && lethalDose.isNotEmpty()
                        )

                        CustomExposedDropdownMenu(
                            Modifier.weight(0.33f),
                            label = stringResource(Res.string.wgk),
                            entries = Wgk.entries,
                            selected = wgk,
                            onSelectedChange = { wgk = it }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomExposedDropdownMenu(
                            Modifier.weight(0.5f),
                            entries = SignalWord.entries,
                            selected = signalWord,
                            onSelectedChange = { signalWord = it },
                            label = stringResource(Res.string.signal_word)
                        )

                        CustomTextField(
                            modifier = Modifier.weight(0.4f),
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = stringResource(Res.string.quantity),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(quantity) && quantity.isNotEmpty()
                        )

                        CustomTextField(
                            modifier = Modifier.weight(0.2f),
                            value = quantityUnit,
                            onValueChange = { quantityUnit = it },
                            label = stringResource(Res.string.unit)
                        )
                    }
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.h_phrases))
                    }
                ) {
                    StatementInput(
                        statements = Statements.hStatements,
                        selectedStatements = hPhrases
                    )
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.p_phrases))
                    }
                ) {
                    StatementInput(
                        statements = Statements.pStatements,
                        selectedStatements = pPhrases
                    )
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.ghs_pictograms))
                    },
                    supportingContent = {
                        Text(stringResource(Res.string.ghs_pictograms_hint))
                    }
                ) {
                    GHSPictogram.entries.chunked(3).forEach { list ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            list.forEach {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = it,
                                    selected = ghsPictograms
                                )
                            }
                        }
                    }
                }
            }

            CustomScrollbar(rememberScrollbarAdapter(scrollState))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun StatementInput(
    statements: Map<String, String>,
    selectedStatements: SnapshotStateList<Pair<String, String>>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        selectedStatements.forEachIndexed { index, phrase ->
            var dropdownExpanded by remember { mutableStateOf(false) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExposedDropdownMenuBox(
                    modifier = Modifier.weight(0.3f),
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        value = phrase.first,
                        singleLine = true,
                        onValueChange = { selectedStatements[index] = phrase.copy(first = it) },
                        isError = phrase.first !in statements.keys && phrase.first.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.heightIn(max = (5 * 48 + 16).dp),
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        val filteredPhrases = remember(phrase.first) { statements.filterKeys { it.contains(phrase.first) } }
                        filteredPhrases.forEach { (id, value) ->
                            DropdownMenuItem(
                                text = {
                                    Text(id)
                                },
                                onClick = {
                                    selectedStatements[index] = phrase.copy(first = id, second = value)
                                    dropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.weight(0.7f),
                    value = phrase.second,
                    onValueChange = { selectedStatements[index] = phrase.copy(second = it) },
                    trailingIcon = {
                        IconButton(
                            onClick = { if (index >= 0 && index <= selectedStatements.lastIndex) selectedStatements.removeAt(index) },
                        ) {
                            Icon(vectorResource(Res.drawable.delete_filled), stringResource(Res.string.delete))
                        }
                    }
                )
            }
        }
        FilledIconButton(
            onClick = { selectedStatements.add(Pair("", "")) },
        ) {
            Icon(vectorResource(Res.drawable.add), stringResource(Res.string.add))
        }
    }
}

@Composable
private fun GHSPictogram(
    modifier: Modifier = Modifier,
    pictogram: GHSPictogram,
    selected: SnapshotStateList<GHSPictogram>
) {
    val isSelected by remember { derivedStateOf { selected.contains(pictogram) } }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .clickable(
                    onClick = { if (isSelected) selected.remove(pictogram) else selected.add(pictogram) },
                )
                .fillMaxSize(0.6f),
            painter = painterResource(pictogram.drawableResource),
            contentDescription = pictogram.alt,
            colorFilter = if (!isSelected) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
        )
    }
}
