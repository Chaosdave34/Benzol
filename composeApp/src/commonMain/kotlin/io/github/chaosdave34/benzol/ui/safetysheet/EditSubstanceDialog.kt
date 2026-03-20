package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
    substance: Substance,
    onDismissRequest: () -> Unit,
    onEdit: (Substance) -> Unit,
) {
    var localSubstance by rememberSaveable { mutableStateOf(substance) }

    if (visible) {
        val temperatureRegex = "(?:(?:ca\\.|>) )?-?\\d+[,.]?\\d*(?: ... -?\\d+[,.]?\\d*)?$".toRegex()
        val numberRegex = "(?:(?:ca\\.|>) )?[0-9]+[,.]?[0-9]*".toRegex()
        val casRegex = "([0-9]{2,8}-[0-9]{2}-[0-9])".toRegex()

        AdaptiveDialog(
            title = stringResource(Res.string.edit_substance_dialog),
            onDismissRequest = onDismissRequest,
            dismissOnClickOutside = false,
            actions = {
                TextButton(
                    onClick = {
                        localSubstance = localSubstance.copyOriginal()
                    }
                ) {
                    Text(stringResource(Res.string.reset))
                }
                TextButton(
                    onClick = {
                        onEdit(localSubstance)
                    }
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
                            value = localSubstance.name,
                            onValueChange = { localSubstance = localSubstance.copy(nameModifiable = localSubstance.nameModifiable.copy(modified = it)) },
                            label = stringResource(Res.string.name)
                        )
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = localSubstance.casNumber,
                            onValueChange = {
                                localSubstance = localSubstance.copy(casNumberModifiable = localSubstance.casNumberModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.cas_number),
                            isError = !casRegex.matches(localSubstance.casNumber) && localSubstance.casNumber.isNotEmpty()
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = localSubstance.molecularFormula,
                            onValueChange = {
                                localSubstance = localSubstance.copy(molecularFormulaModifiable = localSubstance.molecularFormulaModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.formatted_molecular_formula),
                            supportingText = {
                                if (localSubstance.molecularFormula.isBlank()) {
                                    Text(stringResource(Res.string.formatted_molecular_formula_hint))
                                } else {
                                    FormattedMolecularFormula(formula = localSubstance.molecularFormula)
                                }
                            }
                        )
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = localSubstance.molarMass,
                            onValueChange = {
                                localSubstance = localSubstance.copy(molarMassModifiable = localSubstance.molarMassModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.molar_mass),
                            suffix = {
                                Text(stringResource(Res.string.molar_mass_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(localSubstance.molarMass) && localSubstance.molarMass.isNotEmpty()
                        )
                    }
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.properties_and_regulations))
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = localSubstance.meltingPoint,
                            onValueChange = {
                                localSubstance = localSubstance.copy(meltingPointModifiable = localSubstance.meltingPointModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.melting_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !temperatureRegex.matches(localSubstance.meltingPoint) && localSubstance.meltingPoint.isNotEmpty()
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = localSubstance.boilingPoint,
                            onValueChange = {
                                localSubstance = localSubstance.copy(boilingPointModifiable = localSubstance.boilingPointModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.boiling_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !temperatureRegex.matches(localSubstance.boilingPoint) && localSubstance.boilingPoint.isNotEmpty()
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = localSubstance.decompositionTemperature,
                            onValueChange = {
                                localSubstance =
                                    localSubstance.copy(decompositionTemperatureModifiable = localSubstance.decompositionTemperatureModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.decomposition_temperature),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !temperatureRegex.matches(localSubstance.decompositionTemperature) && localSubstance.decompositionTemperature.isNotEmpty()
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = localSubstance.mak,
                            onValueChange = { localSubstance = localSubstance.copy(makModifiable = localSubstance.makModifiable.copy(modified = it)) },
                            label = stringResource(Res.string.mak),
                            suffix = {
                                Text(stringResource(Res.string.mak_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(localSubstance.mak) && localSubstance.mak.isNotEmpty()
                        )

                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = localSubstance.lethalDose,
                            onValueChange = {
                                localSubstance = localSubstance.copy(lethalDoseModifiable = localSubstance.lethalDoseModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.lethal_dose),
                            suffix = {
                                Text(stringResource(Res.string.lethal_dose_unit))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = !numberRegex.matches(localSubstance.lethalDose) && localSubstance.lethalDose.isNotEmpty()
                        )

                        CustomExposedDropdownMenu(
                            Modifier.weight(0.33f),
                            label = stringResource(Res.string.wgk),
                            entries = Wgk.entries,
                            selected = localSubstance.wgk,
                            onSelectedChange = { localSubstance = localSubstance.copy(wgkModifiable = localSubstance.wgkModifiable.copy(modified = it)) }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomExposedDropdownMenu(
                            Modifier.weight(0.5f),
                            entries = SignalWord.entries,
                            selected = localSubstance.signalWord,
                            onSelectedChange = {
                                localSubstance = localSubstance.copy(signalWordModifiable = localSubstance.signalWordModifiable.copy(modified = it))
                            },
                            label = stringResource(Res.string.signal_word)
                        )

                        Row(Modifier.weight(0.6f)) {
                            CustomTextField(
                                modifier = Modifier.weight(0.66f),
                                value = localSubstance.quantity.value,
                                onValueChange = { localSubstance = localSubstance.copy(quantity = localSubstance.quantity.copy(value = it)) },
                                label = stringResource(Res.string.quantity),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                isError = !numberRegex.matches(localSubstance.quantity.value) && localSubstance.quantity.value.isNotEmpty(),
                                shape = RoundedCornerShape(
                                    topStart = ShapeDefaults.ExtraSmall.topStart,
                                    bottomStart = ShapeDefaults.ExtraSmall.bottomStart,
                                    topEnd = CornerSize(0.dp),
                                    bottomEnd = CornerSize(0.dp)
                                ),
                            )

                            CustomTextField(
                                modifier = Modifier.weight(0.33f),
                                value = localSubstance.quantity.unit,
                                onValueChange = { localSubstance = localSubstance.copy(quantity = localSubstance.quantity.copy(unit = it)) },
                                label = stringResource(Res.string.unit),
                                shape = RoundedCornerShape(
                                    topStart = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp),
                                    topEnd = ShapeDefaults.ExtraSmall.topEnd,
                                    bottomEnd = ShapeDefaults.ExtraSmall.bottomEnd
                                )
                            )
                        }
                    }
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.hazard_statements))
                    }
                ) {
                    StatementInput(
                        statements = Statements.hStatements,
                        selectedStatements = localSubstance.hazardStatements,
                        onEdit = {
                            localSubstance = localSubstance.copy(hazardStatementsModifiable = localSubstance.hazardStatementsModifiable.copy(modified = it))
                        }
                    )
                }

                CustomCard(
                    headlineContent = {
                        Text(stringResource(Res.string.precautionary_statements))
                    }
                ) {
                    StatementInput(
                        statements = Statements.pStatements,
                        selectedStatements = localSubstance.precautionaryStatements,
                        onEdit = {
                            localSubstance =
                                localSubstance.copy(precautionaryStatementsModifiable = localSubstance.precautionaryStatementsModifiable.copy(modified = it))
                        }
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
                            list.forEach { pictogram ->
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = pictogram,
                                    checked = pictogram in localSubstance.ghsPictograms,
                                    onCheckedChange = { checked ->
                                        localSubstance = if (checked) {
                                            val set = localSubstance.ghsPictograms.toMutableSet()
                                            set.add(pictogram)
                                            localSubstance.copy(ghsPictogramsModifiable = localSubstance.ghsPictogramsModifiable.copy(modified = set))
                                        } else {
                                            val set = localSubstance.ghsPictograms.toMutableSet()
                                            set.remove(pictogram)
                                            localSubstance.copy(ghsPictogramsModifiable = localSubstance.ghsPictogramsModifiable.copy(modified = set))
                                        }
                                    }
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
    selectedStatements: List<Pair<String, String>>,
    onEdit: (List<Pair<String, String>>) -> Unit
) {
    val localStatements = rememberSaveable(selectedStatements) { selectedStatements.toMutableStateList() }


    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        localStatements.forEachIndexed { index, statement ->
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
                        value = statement.first,
                        singleLine = true,
                        onValueChange = {
                            localStatements[index] = statement.copy(first = it)
                            onEdit(localStatements)
                        },
                        isError = statement.first !in statements.keys && statement.first.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(dropdownExpanded) },
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.heightIn(max = (5 * 48 + 16).dp),
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        val filteredStatements = remember(statement.first) { statements.filterKeys { it.contains(statement.first) } }
                        filteredStatements.forEach { (id, value) ->
                            DropdownMenuItem(
                                text = {
                                    Text(id)
                                },
                                onClick = {
                                    localStatements[index] = statement.copy(first = id, second = value)
                                    dropdownExpanded = false
                                    onEdit(localStatements)
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.weight(0.7f),
                    value = statement.second,
                    onValueChange = {
                        localStatements[index] = statement.copy(second = it)
                        onEdit(localStatements)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (index >= 0 && index <= localStatements.lastIndex) localStatements.removeAt(index)
                                onEdit(localStatements)
                            },
                        ) {
                            Icon(vectorResource(Res.drawable.delete_filled), stringResource(Res.string.delete))
                        }
                    }
                )
            }
        }
        FilledIconButton(
            onClick = {
                localStatements.add(Pair("", ""))
                onEdit(localStatements)
            },
        ) {
            Icon(vectorResource(Res.drawable.add), stringResource(Res.string.add))
        }
    }
}

@Composable
private fun GHSPictogram(
    modifier: Modifier = Modifier,
    pictogram: GHSPictogram,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .clickable(onClick = { onCheckedChange(!checked) })
                .fillMaxSize(0.6f),
            painter = painterResource(pictogram.drawableResource),
            contentDescription = pictogram.alt,
            colorFilter = if (!checked) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null
        )
    }
}
