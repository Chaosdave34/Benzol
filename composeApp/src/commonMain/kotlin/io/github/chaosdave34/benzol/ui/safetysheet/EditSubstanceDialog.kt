package io.github.chaosdave34.benzol.ui.safetysheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.dp
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.data.GHSPictogram
import io.github.chaosdave34.benzol.data.Substance
import io.github.chaosdave34.benzol.ui.CustomScrollbar
import io.github.chaosdave34.benzol.ui.CustomTextField
import io.github.chaosdave34.benzol.ui.adaptive.AdaptiveDialog
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
        var formattedMolecularFormula by remember { mutableStateOf(substance.formattedMolecularFormula) }
        var wgk by remember { mutableStateOf(substance.wgk) }
        var signalWord by remember { mutableStateOf(substance.signalWord) }

        var molarMass by remember { mutableStateOf(substance.molarMass) }
        var lethalDose by remember { mutableStateOf(substance.lethalDose) }
        var mak by remember { mutableStateOf(substance.mak) }
        var meltingPoint by remember { mutableStateOf(substance.meltingPoint) }
        var boilingPoint by remember { mutableStateOf(substance.boilingPoint) }
        var quantity by remember { mutableStateOf(substance.quantity.value) }
        var quantityUnit by remember { mutableStateOf(substance.quantity.unit) }

        val hPhrases = remember { mutableStateListOf<Pair<String, String>>() }
        val pPhrases = remember { mutableStateListOf<Pair<String, String>>() }
        val ghsPictograms = remember { mutableStateListOf<GHSPictogram>() }

        LaunchedEffect(substance) {
            hPhrases.addAll(substance.hPhrases)
            pPhrases.addAll(substance.pPhrases)
            ghsPictograms.addAll(substance.ghsPictograms)
        }

        val onReset: () -> Unit = {
            name = substance.namePair.original
            casNumber = substance.casNumberPair.original
            molecularFormula = substance.molecularFormulaPair.original
            formattedMolecularFormula = substance.formattedMolecularFormulaPair.original
            wgk = substance.wgkPair.original
            signalWord = substance.signalWordPair.original

            molarMass = substance.molarMassPair.original
            lethalDose = substance.lethalDosePair.original
            mak = substance.makPair.original
            meltingPoint = substance.meltingPointPair.original
            boilingPoint = substance.boilingPointPair.original
            quantity = Substance.Quantity().value
            quantityUnit = Substance.Quantity().unit

            hPhrases.clear()
            hPhrases.addAll(substance.hPhrasesPair.original)
            pPhrases.clear()
            pPhrases.addAll(substance.pPhrasesPair.original)
            ghsPictograms.clear()
            ghsPictograms.addAll(substance.ghsPictogramsPair.original)
        }

        val onSave: () -> Unit = {
            onEdit(
                substance.copyAsModified(
                    name.trim(),
                    casNumber.trim(),
                    molecularFormula.trim(),
                    formattedMolecularFormula.trim(),
                    wgk.trim(),
                    signalWord.trim(),
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
                Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                DialogSection(
                    title = stringResource(Res.string.general)
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
                            label = stringResource(Res.string.cas_number)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = molecularFormula,
                            onValueChange = { molecularFormula = it },
                            label = stringResource(Res.string.molecular_formula)
                        )
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = formattedMolecularFormula,
                            onValueChange = { formattedMolecularFormula = it },
                            label = stringResource(Res.string.formatted_molecular_formula),
                            supportingText = {
                                if (formattedMolecularFormula.isBlank()) {
                                    Text(stringResource(Res.string.formatted_molecular_formula_hint))
                                } else {
                                    FormattedMolecularFormula(formula = formattedMolecularFormula)
                                }
                            }
                        )
                    }
                }

                DialogSection(
                    title = stringResource(Res.string.properties)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = wgk,
                            onValueChange = { wgk = it },
                            label = stringResource(Res.string.wgk)
                        )
                        CustomTextField(
                            Modifier.weight(0.5f),
                            value = signalWord,
                            onValueChange = { signalWord = it },
                            label = stringResource(Res.string.signal_word)
                        )
                    }
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
                            }
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = lethalDose,
                            onValueChange = { lethalDose = it },
                            label = stringResource(Res.string.lethal_dose),
                            suffix = {
                                Text(stringResource(Res.string.lethal_dose_unit))
                            }
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = mak,
                            onValueChange = { mak = it },
                            label = stringResource(Res.string.mak),
                            suffix = {
                                Text(stringResource(Res.string.mak_unit))
                            }
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = meltingPoint,
                            onValueChange = { meltingPoint = it },
                            label = stringResource(Res.string.melting_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            }
                        )
                        CustomTextField(
                            Modifier.weight(0.33f),
                            value = boilingPoint,
                            onValueChange = { boilingPoint = it },
                            label = stringResource(Res.string.boiling_point),
                            suffix = {
                                Text(stringResource(Res.string.celsius_unit))
                            }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomTextField(
                            modifier = Modifier.weight(0.4f),
                            value = quantity,
                            onValueChange = { quantity = it },
                            label = stringResource(Res.string.quantity)
                        )
                        Spacer(Modifier.width(10.dp))
                        CustomTextField(
                            modifier = Modifier.weight(0.2f),
                            value = quantityUnit,
                            onValueChange = { quantityUnit = it },
                            label = stringResource(Res.string.unit)
                        )
                        Spacer(Modifier.weight(0.4f))
                    }
                }

                DialogSection(
                    title = stringResource(Res.string.h_phrases)
                ) {
                    PhraseInput(
                        phraseType = PhraseType.H,
                        phrases = hPhrases
                    )
                }

                DialogSection(
                    title = stringResource(Res.string.p_phrases)
                ) {
                    PhraseInput(
                        phraseType = PhraseType.P,
                        phrases = pPhrases
                    )
                }

                DialogSection(
                    title = stringResource(Res.string.ghs_pictograms)
                ) {
                    Text(
                        stringResource(Res.string.ghs_pictograms_hint),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Explosion,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Flame,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.FlameOverCircle,
                            selected = ghsPictograms
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.GasBottle,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Acid,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Skull,
                            selected = ghsPictograms
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Exclamation,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Silhouette,
                            selected = ghsPictograms
                        )
                        GHSPictogram(
                            modifier = Modifier.weight(0.3f),
                            pictogram = GHSPictogram.Nature,
                            selected = ghsPictograms
                        )
                    }
                }
            }

            CustomScrollbar(
                scrollState = scrollState,
                offset = 12.dp
            )
        }
    }
}

private enum class PhraseType {
    H,
    P
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun PhraseInput(
    phraseType: PhraseType,
    phrases: SnapshotStateList<Pair<String, String>>
) {
    var phraseList by remember { mutableStateOf(mapOf<String, String>()) }

    LaunchedEffect(Unit) {
        phraseList = Json.decodeFromString(Res.readBytes("files/phrases.json").decodeToString())
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        phrases.forEachIndexed { index, phrase ->
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
                        onValueChange = { phrases[index] = phrase.copy(first = it) },
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.heightIn(max = (5 * 48 + 16).dp),
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        val filteredPhrases = remember(phrase.first) { phraseList.filterKeys { it.startsWith(phrase.first) && it.startsWith(phraseType.name) } }
                        filteredPhrases.forEach { (id, value) ->
                            DropdownMenuItem(
                                text = {
                                    Text(id)
                                },
                                onClick = {
                                    phrases[index] = phrase.copy(first = id, second = value)
                                    dropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.weight(0.7f),
                    value = phrase.second,
                    onValueChange = { phrases[index] = phrase.copy(second = it) },
                    trailingIcon = {
                        IconButton(
                            onClick = { if (index >= 0 && index <= phrases.lastIndex) phrases.removeAt(index) },
                        ) {
                            Icon(Icons.Filled.Delete, stringResource(Res.string.delete))
                        }
                    }
                )
            }
        }
        FilledIconButton(
            onClick = { phrases.add(Pair("", "")) },
        ) {
            Icon(Icons.Filled.Add, stringResource(Res.string.add))
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun DialogSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMediumEmphasized
            )
            content()
        }
    }
}
