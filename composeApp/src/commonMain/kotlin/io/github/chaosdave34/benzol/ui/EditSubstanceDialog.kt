package io.github.chaosdave34.benzol.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import benzol.composeapp.generated.resources.*
import io.github.chaosdave34.benzol.GHSPictogram
import io.github.chaosdave34.benzol.Substance
import io.github.chaosdave34.benzol.ui.components.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditSubstanceDialog(
    substance: Substance,
    updateSubstance: (Substance) -> Unit,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()

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

    hPhrases.addAll(substance.hPhrases)
    pPhrases.addAll(substance.pPhrases)
    ghsPictograms.addAll(substance.ghsPictograms)

    Dialog(
        onDismissRequest = { onClose() }
    ) {
        Card(
            modifier = Modifier.fillMaxHeight(0.7f),
        ) {
            Column(
                Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(scrollState).weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        DefaultColumn {
                            Input(
                                value = name,
                                onValueChange = { name = it },
                                label = Res.string.name
                            )
                            Input(
                                value = casNumber,
                                onValueChange = { casNumber = it },
                                label = Res.string.cas_number
                            )
                            Input(
                                value = molecularFormula,
                                onValueChange = { molecularFormula = it },
                                label = Res.string.molecular_formula
                            )
                            Input(
                                value = formattedMolecularFormula,
                                onValueChange = { formattedMolecularFormula = it },
                                label = Res.string.formatted_molecular_formula_with_hint,
                                supportingText = {
                                    if (formattedMolecularFormula.isNotBlank()) {
                                        substance.FormattedMolecularFormula(formula = formattedMolecularFormula)
                                    }
                                }
                            )
                            Input(
                                value = wgk,
                                onValueChange = { wgk = it },
                                label = Res.string.wgk
                            )
                            Input(
                                value = signalWord,
                                onValueChange = { signalWord = it },
                                label = Res.string.signal_word
                            )
                        }

                        DefaultColumn {
                            InputWithUnit(
                                value = molarMass,
                                onValueChange = { molarMass = it },
                                label = Res.string.molar_mass,
                                unit = Res.string.molar_mass_unit
                            )
                            InputWithUnit(
                                value = lethalDose,
                                onValueChange = { lethalDose = it },
                                label = Res.string.lethal_dose,
                                unit = Res.string.lethal_dose_unit
                            )
                            InputWithUnit(
                                value = mak,
                                onValueChange = { mak = it },
                                label = Res.string.mak,
                                unit = Res.string.mak_unit
                            )
                            InputWithUnit(
                                value = meltingPoint,
                                onValueChange = { meltingPoint = it },
                                label = Res.string.melting_point,
                                unit = Res.string.celsius_unit
                            )
                            InputWithUnit(
                                value = boilingPoint,
                                onValueChange = { boilingPoint = it },
                                label = Res.string.boiling_point,
                                unit = Res.string.celsius_unit
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Input(
                                    modifier = Modifier.weight(0.8f),
                                    value = quantity,
                                    onValueChange = { quantity = it },
                                    label = Res.string.quantity
                                )
                                Column(
                                    modifier = Modifier.weight(0.2f).padding(start = 10.dp)
                                ) {
                                    Input(
                                        value = quantityUnit,
                                        onValueChange = { quantityUnit = it },
                                        label = Res.string.unit
                                    )
                                }
                            }
                        }

                        PhraseInput(
                            phraseName = Res.string.h_phrases,
                            phrases = hPhrases
                        )
                        PhraseInput(
                            phraseName = Res.string.p_phrases,
                            phrases = pPhrases
                        )

                        DefaultColumn {
                            Text(stringResource(Res.string.ghs_pictograms))
                            Text(stringResource(Res.string.ghs_pictograms_hint))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.EXPLOSION,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.FLAME,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.FLAME_OVER_CIRCLE,
                                    selected = ghsPictograms
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.GAS_BOTTLE,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.ACID,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.SKULL,
                                    selected = ghsPictograms
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.EXCLAMATION,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.SILHOUETTE,
                                    selected = ghsPictograms
                                )
                                GHSPictogram(
                                    modifier = Modifier.weight(0.3f),
                                    pictogram = GHSPictogram.NATURE,
                                    selected = ghsPictograms
                                )
                            }
                        }
                    }

                    Scrollbar(scrollState)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(end = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onClose() }
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
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
                    ) {
                        Icon(Icons.Rounded.Refresh, stringResource(Res.string.reset))
                    }
                    Button(
                        onClick = {
                            updateSubstance(
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
                            onClose()
                        }
                    ) {
                        Icon(Icons.Rounded.Check, stringResource(Res.string.accept))
                    }
                }
            }
        }
    }
}

@Composable
fun InputWithUnit(
    value: String,
    onValueChange: (String) -> Unit,
    label: StringResource,
    unit: StringResource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Input(
            modifier = Modifier.weight(0.8f),
            value = value,
            onValueChange = onValueChange,
            label = label
        )
        Text(
            modifier = Modifier.weight(0.2f),
            text = stringResource(unit),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PhraseInput(
    phraseName: StringResource,
    phrases: SnapshotStateList<Pair<String, String>>
) {
    DefaultColumn {
        Text(stringResource(phraseName))

        phrases.forEachIndexed { index, phrase ->
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(0.3f),
                    value = phrase.first,
                    singleLine = true,
                    onValueChange = { phrases[index] = phrase.copy(first = it) },
                )
                TextField(
                    modifier = Modifier.weight(0.7f),
                    value = phrase.second,
                    singleLine = true,
                    onValueChange = { phrases[index] = phrase.copy(second = it) }
                )
                RemoveListElementButton(
                    list = phrases,
                    index = index
                )
            }
        }
        AddListElementButton(
            list = phrases,
            element = Pair("", "")
        )
    }
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
